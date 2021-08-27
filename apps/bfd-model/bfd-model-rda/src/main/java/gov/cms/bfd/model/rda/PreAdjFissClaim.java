package gov.cms.bfd.model.rda;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/** Java bean for the FissClaimsJson table's claim column */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class PreAdjFissClaim {
  private String dcn;
  private Long sequenceNumber;
  private String hicNo;
  private char currStatus;
  private char currLoc1;
  private String currLoc2;
  private String medaProvId;
  private String medaProv_6;
  private BigDecimal totalChargeAmount;
  private LocalDate receivedDate;
  private LocalDate currTranDate;
  private String admitDiagCode;
  private String principleDiag;
  private String npiNumber;
  private String mbi;
  private String mbiHash;
  private String fedTaxNumber;
  private Instant lastUpdated;
  private String pracLocAddr1;
  private String pracLocAddr2;
  private String pracLocCity;
  private String pracLocState;
  private String pracLocZip;
  private LocalDate stmtCovFromDate;
  private LocalDate stmtCovToDate;
  private String lobCd;

  public enum ServTypeCdMapping {
    Normal,
    Clinic,
    SpecialFacility,
    Unrecognized
  }

  private ServTypeCdMapping servTypeCdMapping;
  private String servTypeCd;

  private String freqCd;
  private String billTypCd;

  @Builder.Default private Set<PreAdjFissProcCode> procCodes = new HashSet<>();
  @Builder.Default private Set<PreAdjFissDiagnosisCode> diagCodes = new HashSet<>();
  @Builder.Default private Set<PreAdjFissPayer> payers = new HashSet<>();
}
