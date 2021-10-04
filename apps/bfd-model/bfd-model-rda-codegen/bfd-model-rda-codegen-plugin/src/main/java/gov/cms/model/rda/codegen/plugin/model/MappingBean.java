package gov.cms.model.rda.codegen.plugin.model;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  private String transformer;
  private TableBean table;
  @Singular private List<EnumTypeBean> enumTypes = new ArrayList<>();
  @Singular private List<FieldBean> fields = new ArrayList<>();
  @Singular private List<ArrayElement> arrays = new ArrayList<>();

  public boolean hasTransformer() {
    return !Strings.isNullOrEmpty(transformer);
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

  public Optional<FieldBean> firstPrimaryKeyField() {
    return fields.stream().findFirst();
  }
}
