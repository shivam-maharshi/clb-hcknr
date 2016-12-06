package com.javacoders.websocketizer.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.ws.rs.Path;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.javacoders.websocketizer.*;

/**
 * This class is responsible for extracting the key service HTTP interface
 * definition information from a JAX-RS application.
 * 
 * @author dedocibula
 * @author shivam.maharshi
 */
public class ServiceExtractor {
  private static final String SPRING = "org.springframework";

  public Collection<ServiceBlueprint> extractBlueprints(File projectDir) {
    final List<ServiceBlueprint> result = new ArrayList<>();

    new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
      try {
        new VoidVisitorAdapter<Object>() {
          private boolean isRestService = false;
          private String packageName;
          private String serviceUrl;

          Collection<ServiceBlueprint> blueprints = new ArrayList<>();

          @Override
          public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            super.visit(n, arg);
            Optional<AnnotationExpr> pathAnnotation = isRESTService(n);
            if (pathAnnotation.isPresent()) {
              isRestService = true;
              serviceUrl = extractFilePath(pathAnnotation.get());
              for (ServiceBlueprint blueprint : blueprints) {
                blueprint.setUrl(serviceUrl + blueprint.getUrl());
                blueprint.setName(n.getName());
                blueprint.setPackge(packageName);
                blueprint.setRequestContext(new RequestContext(packageName + "." + n.getName()));
                String pattern = Pattern.quote(System.getProperty("file.separator"));
                String[] pathSeg = file.getPath().split(pattern);
                String autoGenPath = "";
                for (int i=0; i<pathSeg.length-1; i++) {
                  autoGenPath += pathSeg[i] +"/";
                }
                blueprint.setAutogenPath(autoGenPath);
                pattern = Pattern.quote(System.getProperty("file.separator")+ "src");
                blueprint.setSrcDirPath(file.getPath().split(pattern)[0] + "/src/main/java/");
                result.add(blueprint);
              }
            }
          }

          @Override
          public void visit(PackageDeclaration n, Object arg) {
            super.visit(n, arg);
            packageName = n.getPackageName();
          }

          @Override
          public void visit(CompilationUnit n, Object arg) {
            super.visit(n, arg);
            for (ImportDeclaration declaration : n.getImports()) {
              String imp = declaration.getName().toString();
              String[] parts = imp.split("\\.");
              for (ServiceBlueprint blueprint : blueprints) {
                blueprint.setFramework(imp.contains(SPRING) ? Framework.SPRING : Framework.DEFAULT);
                for (InputParam param : blueprint.getInputs()) {
                  String name = param.getDataType();
                  if (parts[parts.length - 1].equals(name))
                    param.setDataType(imp);
                }
              }
            }
          }

          @Override
          public void visit(MethodDeclaration n, Object arg) {
            super.visit(n, arg);
            if (n.getType() instanceof ReferenceType) {
              String methodUrl = "";
              List<MethodType> methodTypes = new ArrayList<>();
              List<AnnotationExpr> anotations = n.getAnnotations();
              for (AnnotationExpr annotation : anotations) {
                if (annotation instanceof MarkerAnnotationExpr) {
                  populateMethodTypes((MarkerAnnotationExpr) annotation, methodTypes);
                } else if (annotation instanceof SingleMemberAnnotationExpr) {
                  if (annotation.getName().getName().equals("Path")) {
                    methodUrl = ((StringLiteralExpr) ((SingleMemberAnnotationExpr) annotation).getMemberValue())
                        .getValue();
                  }
                }
              }
              for (MethodType methodType : methodTypes) {
                ServiceBlueprint blueprint = new ServiceBlueprint(methodUrl + "/" + methodType.name(), "",
                    fetchMethodInputs(n.getParameters()), new RequestHandler(n.getName(), methodType), null);
                blueprints.add(blueprint);
              }
            }
          }
        }.visit(JavaParser.parse(file), null);
      } catch (ParseException | IOException e) {
        // Do Nothing
      }
    }).explore(projectDir);
    return result;
  }

  private void populateMethodTypes(MarkerAnnotationExpr annotation, List<MethodType> httpMethods) {
    if (MethodType.getEnum(annotation.getName().getName()) != null) {
      httpMethods.add(MethodType.getEnum(annotation.getName().getName()));
    }
  }

  private List<InputParam> fetchMethodInputs(List<Parameter> params) {
    List<InputParam> l = new ArrayList<>();
    for (Parameter param : params) {
      if (param.getAnnotations().size() > 0) {
        AnnotationExpr an = param.getAnnotations().get(0);
        if (an instanceof SingleMemberAnnotationExpr && ((SingleMemberAnnotationExpr) an).getMemberValue() instanceof StringLiteralExpr) {
        if (param.getType() instanceof PrimitiveType) {
          l.add(new InputParam(param.getId().getName(),
              ((StringLiteralExpr) ((SingleMemberAnnotationExpr) an).getMemberValue()).getValue(),
              ((PrimitiveType) param.getType()).getType().name(),
              ParamType.getEnum(an.getName().getName())));
        } else if (param.getType() instanceof ReferenceType) {
          l.add(new InputParam(param.getId().getName(),
              ((StringLiteralExpr) ((SingleMemberAnnotationExpr) an).getMemberValue()).getValue(),
              ((ClassOrInterfaceType) ((ReferenceType) param.getType()).getType()).getName(),
              ParamType.getEnum(an.getName().getName())));  
        }
        }
      } else {
        if (param.getType() instanceof PrimitiveType) {
        l.add(new InputParam(param.getId().getName(), param.getId().getName(),
            ((PrimitiveType) param.getType()).getType().name(), ParamType.BODY));
        } else if (param.getType() instanceof ReferenceType) {
          l.add(new InputParam(param.getId().getName(), param.getId().getName(),
              ((ClassOrInterfaceType) ((ReferenceType) param.getType()).getType()).getName(), ParamType.BODY));
        }
      }
    }
    return l;
  }

  private Optional<AnnotationExpr> isRESTService(ClassOrInterfaceDeclaration declaration) {
    for (AnnotationExpr expr : declaration.getAnnotations()) {
      if (Path.class.getSimpleName().equals(expr.getName().toString()))
        return Optional.of(expr);
    }
    return Optional.empty();
  }

  private String extractFilePath(AnnotationExpr annotation) {
    for (Node node : annotation.getChildrenNodes()) {
      if (node instanceof StringLiteralExpr)
        return ((StringLiteralExpr) node).getValue();
    }
    return "/";
  }
}
