package gov.cms.model.rda.codegen.plugin.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MappingBean {
  private String id;
  private String message;
  private String entity;
  private String transformer;
  private TableBean table;
  @Singular private List<EnumTypeBean> enumTypes = new ArrayList<>();
  @Singular private List<FieldBean> fields = new ArrayList<>();
  @Singular private List<ArrayElement> arrays = new ArrayList<>();

  public boolean hasTransformer() {
    return StringUtils.isNotEmpty(transformer);
  }

  public String entityPackageName() {
    return ModelUtil.packageName(entity);
  }

  public String entityClassName() {
    return ModelUtil.className(entity);
  }

  public String transformerPackageName() {
    return ModelUtil.packageName(transformer);
  }

  public String transformerClassName() {
    return ModelUtil.className(transformer);
  }
}
