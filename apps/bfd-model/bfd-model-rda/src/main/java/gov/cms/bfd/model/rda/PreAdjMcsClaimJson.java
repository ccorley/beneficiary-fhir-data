package gov.cms.bfd.model.rda;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Table(name = "`McsClaimsJson`", schema = "`pre_adj`")
public class PreAdjMcsClaimJson {
  public PreAdjMcsClaimJson(PreAdjMcsClaim claim) {
    this(
        claim.getIdrClmHdIcn(),
        claim.getIdrClaimMbi(),
        claim.getIdrClaimMbiHash(),
        claim.getLastUpdated(),
        claim.getSequenceNumber(),
        claim);
  }

  @Id
  @Column(name = "`idrClmHdIcn`", length = 15, nullable = false)
  @EqualsAndHashCode.Include
  private String idrClmHdIcn;

  @Column(name = "`idrClaimMbi`", length = 13)
  private String idrClaimMbi;

  @Column(name = "`idrClaimMbiHash`", length = 64)
  private String idrClaimMbiHash;

  @Column(name = "`lastUpdated`", nullable = false)
  private Instant lastUpdated;

  @Column(name = "`sequenceNumber`", nullable = false)
  private Long sequenceNumber;

  @Column(name = "`claim`", nullable = false, columnDefinition = "jsonb")
  @Convert(converter = PreAdjMcsClaimConverter.class)
  private PreAdjMcsClaim claim;
}
