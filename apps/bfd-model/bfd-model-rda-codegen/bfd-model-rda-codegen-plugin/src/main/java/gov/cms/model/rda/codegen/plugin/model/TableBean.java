package gov.cms.model.rda.codegen.plugin.model;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TableBean {
  private String name;
  private String schema;
  private List<String> primaryKeyFields = new ArrayList<>();

  public boolean hasSchema() {
    return !Strings.isNullOrEmpty(schema);
  }

  public boolean hasPrimaryKey() {
    return primaryKeyFields.size() > 0;
  }

  public boolean isPrimaryKey(String name) {
    return primaryKeyFields.stream().anyMatch(fieldName -> fieldName.equals(name));
  }
}
