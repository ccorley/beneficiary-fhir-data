package gov.cms.model.rda.codegen.plugin.model;

import java.util.List;
import lombok.Data;

@Data
public class RootBean {
  private List<MappingBean> mappings;
}
