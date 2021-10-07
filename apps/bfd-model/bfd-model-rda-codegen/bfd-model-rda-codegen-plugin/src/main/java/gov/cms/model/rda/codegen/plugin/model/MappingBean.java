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
  private String messageClassName;
  private String entityClassName;
  private String transformerClassName;
  private TableBean table;
  @Singular private List<EnumTypeBean> enumTypes = new ArrayList<>();
  @Singular private List<TransformationBean> transformations = new ArrayList<>();
  @Singular private List<ArrayElement> arrays = new ArrayList<>();

  public boolean hasTransformer() {
    return !Strings.isNullOrEmpty(transformerClassName);
  }

  public String entityPackageName() {
    return ModelUtil.packageName(entityClassName);
  }

  public String entityClassName() {
    return ModelUtil.className(entityClassName);
  }

  public String transformerPackage() {
    return ModelUtil.packageName(transformerClassName);
  }

  public String transformerSimpleName() {
    return ModelUtil.className(transformerClassName);
  }

  public Optional<TransformationBean> firstPrimaryKeyField() {
    return transformations.stream().findFirst();
  }
}
