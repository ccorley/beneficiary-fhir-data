package gov.cms.bfd.server.war.r4.providers;

import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.newrelic.api.agent.Trace;
import gov.cms.bfd.server.war.commons.LoadedFilterManager;
import gov.cms.bfd.server.war.commons.PreAdjClaimDao;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ClaimResponse;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.stereotype.Component;

/** This FHIR {@link IResourceProvider} adds support for R4 {@link ClaimResponse} resources. */
@Component
public class R4ClaimResponseResourceProvider implements IResourceProvider {

  /**
   * A {@link Pattern} that will match the {@link ClaimResponse#getId()}s used in this application,
   * e.g. <code>f-1234</code> or <code>m--1234</code> (for negative IDs).
   */
  private static final Pattern CLAIM_ID_PATTERN = Pattern.compile("([fm])-(-?\\p{Alnum}+)");

  private EntityManager entityManager;
  private MetricRegistry metricRegistry;
  private LoadedFilterManager loadedFilterManager;

  private PreAdjClaimDao preAdjClaimDao;

  /** @param entityManager a JPA {@link EntityManager} connected to the application's database */
  @PersistenceContext
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /** @param metricRegistry the {@link MetricRegistry} to use */
  @Inject
  public void setMetricRegistry(MetricRegistry metricRegistry) {
    this.metricRegistry = metricRegistry;
  }

  /** @param loadedFilterManager the {@link LoadedFilterManager} to use */
  @Inject
  public void setLoadedFilterManager(LoadedFilterManager loadedFilterManager) {
    this.loadedFilterManager = loadedFilterManager;
  }

  /** @see IResourceProvider#getResourceType() */
  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return ClaimResponse.class;
  }

  @PostConstruct
  public void init() {
    preAdjClaimDao = new PreAdjClaimDao(entityManager, metricRegistry);
  }

  /**
   * Adds support for the FHIR "read" operation, for {@link ClaimResponse}s. The {@link Read}
   * annotation indicates that this method supports the read operation.
   *
   * <p>Read operations take a single parameter annotated with {@link IdParam}, and should return a
   * single resource instance.
   *
   * @param claimId The read operation takes one parameter, which must be of type {@link IdType} and
   *     must be annotated with the {@link IdParam} annotation.
   * @return Returns a resource matching the specified {@link IdDt}, or <code>null</code> if none
   *     exists.
   */
  @Read(version = false)
  @Trace
  public ClaimResponse read(@IdParam IdType claimId, RequestDetails requestDetails) {
    if (claimId == null) throw new IllegalArgumentException("Resource ID can not be null");
    if (claimId.getVersionIdPartAsLong() != null)
      throw new IllegalArgumentException("Resource ID must not define a version.");

    String claimIdText = claimId.getIdPart();
    if (claimIdText == null || claimIdText.trim().isEmpty())
      throw new IllegalArgumentException("Resource ID can not be null/blank");

    Matcher claimIdMatcher = CLAIM_ID_PATTERN.matcher(claimIdText);
    if (!claimIdMatcher.matches())
      throw new IllegalArgumentException("Unsupported ID pattern: " + claimIdText);

    String claimIdTypeText = claimIdMatcher.group(1);
    Optional<IPreAdjClaimResponseTypeV2> optional = parseClaimType(claimIdTypeText);
    if (!optional.isPresent()) throw new ResourceNotFoundException(claimId);
    IPreAdjClaimResponseTypeV2 claimIdType = optional.get();
    String claimIdString = claimIdMatcher.group(2);

    Object claimEntity;

    try {
      claimEntity = preAdjClaimDao.getEntityById(claimIdType, claimIdString);
    } catch (NoResultException e) {
      throw new ResourceNotFoundException(claimId);
    }

    return claimIdType.getTransformer().transform(metricRegistry, claimEntity);
  }

  /**
   * Helper method to make mocking easier in tests.
   *
   * @param typeText String to parse representing the claim type.
   * @return The parsed {@link PreAdjClaimResponseTypeV2} type.
   */
  @VisibleForTesting
  Optional<IPreAdjClaimResponseTypeV2> parseClaimType(String typeText) {
    return PreAdjClaimResponseTypeV2.parse(typeText);
  }
}
