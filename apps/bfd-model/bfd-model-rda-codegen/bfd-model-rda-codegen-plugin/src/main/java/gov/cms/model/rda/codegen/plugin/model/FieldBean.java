package gov.cms.model.rda.codegen.plugin.model;

import lombok.Data;

@Data
public class FieldBean {
  private String from;
  private String to;
  private ColumnBean column;

  public String getTo() {
    return to == null ? from : to;
  }
}
