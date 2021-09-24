package gov.cms.model.rda.codegen.plugin.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MappingBean {
  private String id;
  private String message;
  private String entity;
  private TableBean table;
  private List<EnumTypeBean> enumTypes = new ArrayList<>();
  private List<FieldBean> fields = new ArrayList<>();
  private List<ArrayElement> arrays = new ArrayList<>();

  public String computePackageName() {
    return entity.substring(0, entity.lastIndexOf("."));
  }

  public String computeClassName() {
    return entity.substring(1 + entity.lastIndexOf("."));
  }
}
