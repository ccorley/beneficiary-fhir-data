package gov.cms.bfd.model.rda;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/** Java bean for the McsClaimsJson table's claim column */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class PreAdjMcsClaim {
  private String idrClmHdIcn;
  private Long sequenceNumber;
  private String idrContrId;
  private String idrHic;
  private String idrClaimType;
  private Integer idrDtlCnt;
  private String idrBeneLast_1_6;
  private String idrBeneFirstInit;
  private String idrBeneMidInit;
  private String idrBeneSex;
  private String idrStatusCode;
  private LocalDate idrStatusDate;
  private String idrBillProvNpi;
  private String idrBillProvNum;
  private String idrBillProvEin;
  private String idrBillProvType;
  private String idrBillProvSpec;
  private String idrBillProvGroupInd;
  private String idrBillProvPriceSpec;
  private String idrBillProvCounty;
  private String idrBillProvLoc;
  private BigDecimal idrTotAllowed;
  private BigDecimal idrCoinsurance;
  private BigDecimal idrDeductible;
  private String idrBillProvStatusCd;
  private BigDecimal idrTotBilledAmt;
  private LocalDate idrClaimReceiptDate;
  private String idrClaimMbi;
  private String idrClaimMbiHash;
  private LocalDate idrHdrFromDateOfSvc;
  private LocalDate idrHdrToDateOfSvc;
  private Instant lastUpdated;

  @Builder.Default private List<PreAdjMcsDetail> details = new ArrayList<>();
  @Builder.Default private List<PreAdjMcsDiagnosisCode> diagCodes = new ArrayList<>();
}
