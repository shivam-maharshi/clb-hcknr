package com.javacoders.websocketizer.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.javacoders.websocketizer.InputParam;
import com.javacoders.websocketizer.RequestContext;
import com.javacoders.websocketizer.RequestHandler;
import com.javacoders.websocketizer.ServiceBlueprint;

import javax.ws.rs.Path;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Author: dedocibula
 * Created on: 17.10.2016.
 */
public class ServiceExtractor {
    public static void main(String[] args) {
        String dir = ".\\src\\main\\java\\com\\javacoders\\service\\rs";
        File projectDir = new File(dir);
        ServiceExtractor extractor = new ServiceExtractor();
        Collection<ServiceBlueprint> blueprints = extractor.extractBlueprints(projectDir);
    }

    public Collection<ServiceBlueprint> extractBlueprints(File projectDir) {
        final List<ServiceBlueprint> result = new ArrayList<>();

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            System.out.println(path);
            try {
                new VoidVisitorAdapter<Object>() {
                    private boolean isRestService = false;

                    private String serviceUrl;
                    private String packageName;

                    Collection<ServiceBlueprint> blueprints = new ArrayList<>();

                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        Optional<AnnotationExpr> pathAnnotation = isRESTService(n);
                        if (pathAnnotation.isPresent()) {
                            isRestService = true;
                            serviceUrl = extractFilePath(pathAnnotation.get());
//                            System.out.println(serviceUrl);
                            for (ServiceBlueprint blueprint : blueprints) {
                                blueprint.setUrl(serviceUrl);
                                blueprint.setRequestContext(new RequestContext(packageName + "." + n.getName()));
                                result.add(blueprint);
                            }
//                            System.out.println(classNameBuilder.toString());
                        }
                    }

                    @Override
                    public void visit(PackageDeclaration n, Object arg) {
                        super.visit(n, arg);
                        packageName = n.getPackageName();
                    }

                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);
                        ServiceBlueprint blueprint = new ServiceBlueprint(null, new ArrayList<>(), new RequestHandler(n.getName()), null);
                        blueprints.add(blueprint);
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (ParseException | IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
        return result;
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
                return ((StringLiteralExpr)node).getValue();
        }
        return "/";
    }
}
