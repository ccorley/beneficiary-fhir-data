package gov.cms.model.rda.codegen.plugin.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class EnumTypeBean {
  private String name;
  private List<String> values = new ArrayList<>();
}
