package gov.cms.model.rda.codegen.plugin.model;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableBean {
  private String name;
  private String schema;
  @Singular private List<String> primaryKeyColumns = new ArrayList<>();
  @Singular private List<ColumnBean> columns = new ArrayList<>();

  public ColumnBean findColumn(String columnName) {
    return columns.stream()
        .filter(c -> columnName.equals(c.getName()))
        .findAny()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format(
                        "reference to non-existent column %s in table %s", columnName, name)));
  }

  public boolean hasSchema() {
    return !Strings.isNullOrEmpty(schema);
  }

  public boolean hasPrimaryKey() {
    return primaryKeyColumns.size() > 0;
  }

  public boolean isPrimaryKey(String name) {
    return primaryKeyColumns.stream().anyMatch(fieldName -> fieldName.equals(name));
  }
}
