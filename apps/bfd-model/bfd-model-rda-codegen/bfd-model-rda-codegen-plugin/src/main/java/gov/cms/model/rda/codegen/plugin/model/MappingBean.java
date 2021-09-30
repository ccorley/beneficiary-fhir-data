package gov.cms.model.rda.codegen.plugin.model;

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
public class MappingBean {
  private String id;
  private String message;
  private String entity;
  private TableBean table;
  @Singular private List<EnumTypeBean> enumTypes = new ArrayList<>();
  @Singular private List<FieldBean> fields = new ArrayList<>();
  @Singular private List<ArrayElement> arrays = new ArrayList<>();

  public String computePackageName() {
    return entity.substring(0, entity.lastIndexOf("."));
  }

  public String computeClassName() {
    return entity.substring(1 + entity.lastIndexOf("."));
  }
}
