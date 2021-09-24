package gov.cms.model.rda.codegen.plugin.model;

import java.util.List;
import java.util.Optional;
import lombok.Data;

@Data
public class RootBean {
  private List<MappingBean> mappings;

  public Optional<MappingBean> findMappingWithId(String id) {
    return mappings.stream().filter(m -> m.getId().equals(id)).findAny();
  }
}
