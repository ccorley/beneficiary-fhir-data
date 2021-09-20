package gov.cms.bfd.model.rda;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class PreAdjFissDiagnosisCode {
  private String dcn;
  private short priority;
  private String diagCd2;
  private String diagPoaInd;
  private String bitFlags;
  private Instant lastUpdated;
}
