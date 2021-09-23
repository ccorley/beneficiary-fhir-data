package gov.cms.model.rda.codegen.plugin.model;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class TableBean {
  private String name;
  private String schema;
  private List<String> primaryKeyFields = new ArrayList<>();

  public boolean hasSchema() {
    return !Strings.isNullOrEmpty(schema);
  }

  public Set<String> computePrimaryKeys() {
    return ImmutableSet.copyOf(primaryKeyFields);
  }
}
