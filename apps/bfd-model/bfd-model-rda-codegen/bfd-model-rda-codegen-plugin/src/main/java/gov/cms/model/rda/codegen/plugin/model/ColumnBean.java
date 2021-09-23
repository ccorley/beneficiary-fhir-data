package gov.cms.model.rda.codegen.plugin.model;

import com.google.common.base.Strings;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.SneakyThrows;

@Data
public class ColumnBean {
  private String name;
  private String sqlType;
  private String javaType;
  private boolean nullable = true;

  public String getColumnName(String fieldName) {
    return Strings.isNullOrEmpty(name) ? fieldName : name;
  }

  public int computeLength() {
    Matcher matcher = Pattern.compile("char\\((\\d+)\\)").matcher(sqlType);
    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    } else {
      return 0;
    }
  }

  public TypeName computeJavaType() {
    if (Strings.isNullOrEmpty(javaType)) {
      return mapSqlTypeToTypeName();
    } else {
      return mapJavaTypeToTypeName();
    }
  }

  private TypeName mapSqlTypeToTypeName() {
    if (sqlType.contains("char")) {
      return ClassName.get(String.class);
    }
    if (sqlType.contains("int")) {
      return ClassName.get(Long.class);
    }
    if (sqlType.contains("decimal")) {
      return ClassName.get(BigDecimal.class);
    }
    if (sqlType.contains("date")) {
      return ClassName.get(LocalDate.class);
    }
    if (sqlType.contains("timestamp")) {
      return ClassName.get(Instant.class);
    }
    throw new RuntimeException("no mapping for sqlType " + sqlType);
  }

  @SneakyThrows(ClassNotFoundException.class)
  private TypeName mapJavaTypeToTypeName() {
    switch (javaType) {
      case "char":
        return TypeName.CHAR;
      case "int":
        return TypeName.INT;
      case "long":
        return TypeName.LONG;
      default:
        return ClassName.get(Class.forName(javaType));
    }
  }
}
