package gov.cms.bfd.server.war.r4.providers.preadj.common;

import ca.uhn.fhir.rest.param.DateRangeParam;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.annotations.VisibleForTesting;
import gov.cms.bfd.server.war.commons.QueryUtils;
import gov.cms.bfd.server.war.r4.providers.TransformerUtilsV2;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/** Provides common logic for performing DB interactions */
public class ClaimDao {

  private static final String CLAIM_BY_MBI_METRIC_QUERY = "claim_by_mbi";
  private static final String CLAIM_BY_MBI_METRIC_NAME =
      MetricRegistry.name(ClaimDao.class.getSimpleName(), "query", CLAIM_BY_MBI_METRIC_QUERY);
  private static final String CLAIM_BY_ID_METRIC_QUERY = "claim_by_id";
  private static final String CLAIM_BY_ID_METRIC_NAME =
      MetricRegistry.name(ClaimDao.class.getSimpleName(), "query", CLAIM_BY_ID_METRIC_QUERY);

  private final EntityManager entityManager;
  private final MetricRegistry metricRegistry;

  public ClaimDao(EntityManager entityManager, MetricRegistry metricRegistry) {
    this.entityManager = entityManager;
    this.metricRegistry = metricRegistry;
  }

  /**
   * Gets an entity by it's ID for the given claim type.
   *
   * @param type The type of claim to retrieve.
   * @param id The id of the claim to retrieve.
   * @return An entity object of the given type provided in {@link ResourceTypeV2}
   */
  public Object getEntityById(ResourceTypeV2<?> type, String id) {
    return getEntityById(type.getEntityClass(), type.getEntityIdAttribute(), id);
  }

  /**
   * Gets an entity by it's ID for the given claim type.
   *
   * @param entityClass The type of entity to retrieve.
   * @param entityIdAttribute The name of the entity's id attribute.
   * @param id The id value of the claim to retrieve.
   * @param <T> The entity type of the claim.
   * @return The retrieved entity of the given type for the requested claim id.
   */
  @VisibleForTesting
  <T> T getEntityById(Class<T> entityClass, String entityIdAttribute, String id) {
    T claimEntity = null;

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<T> criteria = builder.createQuery(entityClass);
    Root<T> root = criteria.from(entityClass);

    criteria.select(root);
    criteria.where(builder.equal(root.get(entityIdAttribute), id));

    Timer.Context timerClaimQuery = metricRegistry.timer(CLAIM_BY_ID_METRIC_NAME).time();
    try {
      claimEntity = entityManager.createQuery(criteria).getSingleResult();
    } finally {
      long claimByIdQueryNanoSeconds = timerClaimQuery.stop();
      TransformerUtilsV2.recordQueryInMdc(
          CLAIM_BY_ID_METRIC_QUERY, claimByIdQueryNanoSeconds, claimEntity == null ? 0 : 1);
    }

    return claimEntity;
  }

  public <T> List<T> findAllByMbiHash(
      Class<T> entityClass, String mbiHash, DateRangeParam lastUpdated) {
    return findAllByAttribute(entityClass, "mbiHash", mbiHash, lastUpdated);
  }

  public <T> List<T> findAllByMbi(Class<T> entityClass, String mbi, DateRangeParam lastUpdated) {
    return findAllByAttribute(entityClass, "mbi", mbi, lastUpdated);
  }

  @VisibleForTesting
  <T> List<T> findAllByAttribute(
      Class<T> entityClass,
      String attributeName,
      String attributeValue,
      DateRangeParam lastUpdated) {
    List<T> claimEntities = null;

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<T> criteria = builder.createQuery(entityClass);
    Root<T> root = criteria.from(entityClass);

    criteria.select(root);
    criteria.where(
        builder.and(
            builder.equal(root.get(attributeName), attributeValue),
            lastUpdated == null
                ? builder.and()
                : createDateRangePredicate(root, lastUpdated, builder)));

    Timer.Context timerClaimQuery = metricRegistry.timer(CLAIM_BY_MBI_METRIC_NAME).time();
    try {
      claimEntities = entityManager.createQuery(criteria).getResultList();
    } finally {
      long claimByIdQueryNanoSeconds = timerClaimQuery.stop();
      TransformerUtilsV2.recordQueryInMdc(
          CLAIM_BY_MBI_METRIC_QUERY,
          claimByIdQueryNanoSeconds,
          claimEntities == null || claimEntities.isEmpty() ? 0 : 1);
    }

    return claimEntities;
  }

  @VisibleForTesting
  Predicate createDateRangePredicate(
      Root<?> root, DateRangeParam dateRange, CriteriaBuilder builder) {
    return QueryUtils.createLastUpdatedPredicateInstant(builder, root, dateRange);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClaimDao claimDao = (ClaimDao) o;
    return Objects.equals(entityManager, claimDao.entityManager)
        && Objects.equals(metricRegistry, claimDao.metricRegistry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityManager, metricRegistry);
  }
}
