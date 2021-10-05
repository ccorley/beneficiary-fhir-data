package gov.cms.model.rda.codegen.plugin.model;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldBean {
  private String from;
  private String to;
  @Builder.Default private boolean optional = true;
  private ColumnBean column;
  private String transformer;

  public String getTo() {
    return to == null ? from : to;
  }

  public boolean hasTransformer() {
    return !Strings.isNullOrEmpty(transformer);
  }
}
