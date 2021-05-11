package gov.cms.bfd.server.war.r4.providers;

import gov.cms.bfd.model.rif.Beneficiary;
import gov.cms.bfd.server.war.commons.PreAdjClaimTypeTransformerV2;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.hl7.fhir.r4.model.ClaimResponse;

/**
 * Enumerates the various Beneficiary FHIR Data Server (BFD) claim types that are supported by
 * {@link R4ClaimResponseResourceProvider}.
 */
public enum PreAdjClaimTypeV2 {

    // TODO: Complete null fields when entity available
    F(null, null, null, (entity) -> null, PreAdjFissClaimTransformerV2::transform),

    M(null, null, null, (entity) -> null, PreAdjMcsClaimTransformerV2::transform);

    private final Class<?> entityClass;
    private final SingularAttribute<?, ?> entityIdAttribute;
    private final SingularAttribute<?, String> entityBeneficiaryIdAttribute;
    private final Function<Object, LocalDate> serviceEndAttributeFunction;
    private final PreAdjClaimTypeTransformerV2 transformer;
    private final Collection<PluralAttribute<?, ?, ?>> entityLazyAttributes;

    /**
     * Enum constant constructor.
     *
     * @param entityClass                  the value to use for {@link #getEntityClass()}
     * @param entityIdAttribute            ibute the value to u e for {@link #getEntityIdAttribute()}
     * @param entityBeneficiaryIdAttribute tribute the value to use for {@link
     *                                     #getEntityBeneficiaryIdAttribute()}
     * @param transformer                  the value to use for {@link #getTransformer()}
     * @param entityLazyAttributes         the value to use for {@link #getEntityLazyAttributes()}
     */
    PreAdjClaimTypeV2(
            Class<?> entityClass,
            SingularAttribute<?, ?> entityIdAttribute,
            SingularAttribute<?, String> entityBeneficiaryIdAttribute,
            Function<Object, LocalDate> serviceEndAttributeFunction,
            PreAdjClaimTypeTransformerV2 transformer,
            PluralAttribute<?, ?, ?>... entityLazyAttributes) {
        this.entityClass = entityClass;
        this.entityIdAttribute = entityIdAttribute;
        this.entityBeneficiaryIdAttribute = entityBeneficiaryIdAttribute;
        this.serviceEndAttributeFunction = serviceEndAttributeFunction;
        this.transformer = transformer;
        this.entityLazyAttributes =
                entityLazyAttributes != null
                        ? Collections.unmodifiableCollection(Arrays.asList(entityLazyAttributes))
                        : Collections.emptyList();
    }

    /**
     * @return the JPA {@link Entity} {@link Class} used to store instances of this {@link
     * PreAdjClaimTypeV2} in the database
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * @return the JPA {@link Entity} field used as the entity's {@link Id}
     */
    public SingularAttribute<?, ?> getEntityIdAttribute() {
        return entityIdAttribute;
    }

    /**
     * @return the JPA {@link Entity} field that is a (foreign keyed) reference to {@link
     * Beneficiary#getBeneficiaryId()}
     */
    public SingularAttribute<?, String> getEntityBeneficiaryIdAttribute() {
        return entityBeneficiaryIdAttribute;
    }

    /**
     * @return the {@link Function} to use to retrieve the {@link LocalDate} to use for service date
     * filter
     */
    public Function<Object, LocalDate> getServiceEndAttributeFunction() {
        return serviceEndAttributeFunction;
    }

    /**
     * @return the {@link PreAdjClaimTypeTransformerV2} to use to transform the JPA {@link Entity}
     * instances into FHIR {@link ClaimResponse} instances
     */
    public PreAdjClaimTypeTransformerV2 getTransformer() {
        return transformer;
    }

    /**
     * @return the {@link PluralAttribute}s in the JPA {@link Entity} that are {@link FetchType#LAZY}
     */
    public Collection<PluralAttribute<?, ?, ?>> getEntityLazyAttributes() {
        return entityLazyAttributes;
    }

    /**
     * @param claimTypeText the lower-cased {@link PreAdjClaimTypeV2#name()} value to parse back into
     *                      a {@link PreAdjClaimTypeV2}
     * @return the {@link PreAdjClaimTypeV2} represented by the specified {@link String}
     */
    public static Optional<PreAdjClaimTypeV2> parse(String claimTypeText) {
        for (PreAdjClaimTypeV2 claimType : PreAdjClaimTypeV2.values())
            if (claimType.name().toLowerCase().equals(claimTypeText)) return Optional.of(claimType);
        return Optional.empty();
    }
}
