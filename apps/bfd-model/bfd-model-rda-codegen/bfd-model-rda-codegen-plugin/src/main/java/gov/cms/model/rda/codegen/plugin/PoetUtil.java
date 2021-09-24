package gov.cms.model.rda.codegen.plugin;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec.Builder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public class PoetUtil {
  public static void addSetter(FieldSpec fieldSpec, Builder classBuilder) {
    String setterName = fieldToMethodName("set", fieldSpec.name);
    MethodSpec.Builder methodBuilder =
        MethodSpec.methodBuilder(setterName).addModifiers(Modifier.PUBLIC);
    methodBuilder.addParameter(fieldSpec.type, fieldSpec.name);
    methodBuilder.addStatement("this." + fieldSpec.name + "=" + fieldSpec.name);
    classBuilder.addMethod(methodBuilder.build());
  }

  public static void addGetter(FieldSpec fieldSpec, Builder classBuilder) {
    String getterName = fieldToMethodName("get", fieldSpec.name);
    MethodSpec.Builder methodBuilder =
        MethodSpec.methodBuilder(getterName).returns(fieldSpec.type).addModifiers(Modifier.PUBLIC);
    methodBuilder.addStatement("return this." + fieldSpec.name);
    classBuilder.addMethod(methodBuilder.build());
  }

  public static void addHashCode(List<FieldSpec> fields, Builder classBuilder) {
    MethodSpec.Builder hashCodeMethod =
        MethodSpec.methodBuilder("hashCode")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .addStatement(
                "return $T.hash($L)",
                Objects.class,
                fields.stream().map(f -> f.name).collect(Collectors.joining(", ")));
    classBuilder.addMethod(hashCodeMethod.build());
  }

  public static void addEquals(TypeName typeName, List<FieldSpec> fields, Builder classBuilder) {
    MethodSpec.Builder equalsMethod =
        MethodSpec.methodBuilder("equals")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(Object.class, "obj")
            .returns(boolean.class);

    equalsMethod.beginControlFlow("if (this == obj)").addStatement("return true").endControlFlow();
    equalsMethod.beginControlFlow("if (obj == null)").addStatement("return false").endControlFlow();
    equalsMethod
        .beginControlFlow("if (getClass() != obj.getClass())")
        .addStatement("return false")
        .endControlFlow();
    equalsMethod.addStatement("$T other = ($T) obj", typeName, typeName);
    for (FieldSpec field : fields) {
      equalsMethod
          .beginControlFlow("if (!$T.equals($N, other.$N))", Objects.class, field, field)
          .addStatement("return false")
          .endControlFlow();
    }
    equalsMethod.addStatement("return true");

    classBuilder.addMethod(equalsMethod.build());
  }

  private static String fieldToMethodName(String prefix, String fieldName) {
    return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }
}
