/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

package org.codeontology.extraction;

import org.codeontology.CodeOntology;
import org.codeontology.build.DefaultProject;
import org.codeontology.build.gradle.AndroidProject;
import org.codeontology.build.gradle.GradleProject;
import org.codeontology.build.maven.MavenProject;
import org.codeontology.extraction.declaration.*;
import org.codeontology.extraction.expression.AssignmentExpressionEntity;
import org.codeontology.extraction.expression.ClassInstanceCreationExpression;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.expression.MethodInvocationExpressionEntity;
import org.codeontology.extraction.project.DefaultProjectEntity;
import org.codeontology.extraction.project.GradleProjectEntity;
import org.codeontology.extraction.project.JarFileEntity;
import org.codeontology.extraction.project.MavenProjectEntity;
import org.codeontology.extraction.statement.*;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class EntityFactory {

    private static EntityFactory instance;

    private EntityFactory() {

    }

    public static EntityFactory getInstance() {
        if (instance == null) {
            instance = new EntityFactory();
        }
        return instance;
    }

    public PackageEntity wrap(CtPackage pack) {
        return new PackageEntity(pack);
    }

    public PackageEntity wrap(CtPackageReference pack) {
        return new PackageEntity(pack);
    }

    public FieldEntity wrap(CtField<?> field) {
        return new FieldEntity(field);
    }

    public FieldEntity wrap(CtFieldReference<?> field) {
        return new FieldEntity(field);
    }

    public MethodEntity wrap(CtMethod<?> method) {
        return new MethodEntity(method);
    }

    public TypeEntity wrap(CtType<?> type) {
        return wrap(type.getReference());
    }

    public TypeEntity wrap(CtTypeReference<?> reference) {
        TypeEntity<?> entity = null;

        if (reference.getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            return null;
        }

        TypeKind kind = TypeKind.getKindOf(reference);
        if (kind == null) {
            return null;
        }

        switch (kind) {
            case CLASS:
                entity = new ClassEntity<>(reference);
                break;
            case INTERFACE:
                entity = new InterfaceEntity(reference);
                break;
            case ENUM:
                entity = new EnumEntity<>(reference);
                break;
            case ANNOTATION:
                entity = new AnnotationEntity(reference);
                break;
            case PRIMITIVE:
                entity = new PrimitiveTypeEntity(reference);
                break;
            case ARRAY:
                entity = new ArrayEntity(reference);
                break;
            case TYPE_VARIABLE:
                entity = new TypeVariableEntity(reference);
                break;
            case PARAMETERIZED_TYPE:
                if (CodeOntology.processGenerics()) {
                    entity = new ParameterizedTypeEntity(reference);
                } else {
                    entity = new ParameterizedTypeEntity(reference).getGenericType();
                }
                break;
        }
        return entity;
    }

    public LocalVariableEntity wrap(CtLocalVariable<?> variable) {
        return new LocalVariableEntity(variable);
    }

    public ParameterEntity wrap(CtParameter<?> parameter) {
        try {
            return new ParameterEntity(parameter);
        } catch (NullTypeException e) {
            return null;
        }
    }

    public ParameterEntity wrapByTypeReference(CtTypeReference<?> reference) {
        return new ParameterEntity(reference);
    }

    public ConstructorEntity wrap(CtConstructor<?> constructor) {
        return new ConstructorEntity(constructor);
    }

    public ExecutableEntity<?> wrap(CtExecutableReference<?> reference) {
        if (reference.isConstructor()) {
            return new ConstructorEntity(reference);
        } else {
            return new MethodEntity(reference);
        }
    }

    public LambdaEntity wrap(CtLambda<?> lambda) {
        return new LambdaEntity(lambda);
    }

    public TypeVariableEntity wrap(TypeVariable typeVariable) {
        CtTypeParameterReference reference = reflectionFactory().createTypeVariableReference(typeVariable);
        return new TypeVariableEntity(reference);
    }

    public ArrayEntity wrap(GenericArrayType array) {
        CtTypeReference<?> reference = reflectionFactory().createGenericArrayReference(array);
        return new ArrayEntity(reference);
    }

    public ParameterizedTypeEntity wrap(ParameterizedType parameterizedType) {
        CtTypeReference reference = reflectionFactory().createParameterizedTypeReference(parameterizedType);
        return new ParameterizedTypeEntity(reference);
    }

    public TypeEntity<?> wrap(Type type) {
        return wrap(reflectionFactory().createTypeReference(type));
    }

    public DefaultProjectEntity wrap(DefaultProject project) {
        return new DefaultProjectEntity(project);
    }

    public GradleProjectEntity wrap(GradleProject project) {
        return new GradleProjectEntity(project);
    }

    public MavenProjectEntity wrap(MavenProject project) {
        return new MavenProjectEntity(project);
    }

    public GradleProjectEntity wrap(AndroidProject project) {
        return new GradleProjectEntity(project);
    }

    public JarFileEntity wrap(JarFile jarFile) {
        return new JarFileEntity(jarFile);
    }

    private ReflectionFactory reflectionFactory() {
        return ReflectionFactory.getInstance();
    }

    /************************************************************
     *                                                          *
     *              STATEMENTS AND EXPRESSIONS                  *
     *                                                          *
     ************************************************************/

    public StatementEntity<?> wrap(CtStatement statement) {

        switch (StatementKind.getKindOf(statement)) {
            case BLOCK:
                return new BlockEntity((CtBlock) statement);
            case IF_THEN_ELSE:
                return new IfThenElseEntity((CtIf) statement);
            case SWITCH:
                return new SwitchEntity((CtSwitch) statement);
            case WHILE:
                return new WhileEntity((CtWhile) statement);
            case DO:
                return new DoWhileEntity((CtDo) statement);
            case FOR:
                return new ForEntity((CtFor) statement);
            case FOREACH:
                return new ForEachEntity((CtForEach) statement);
            case TRY:
                return new TryEntity((CtTry) statement);
            case RETURN:
                return new ReturnEntity((CtReturn<?>) statement);
            case THROW:
                return new ThrowEntity((CtThrow) statement);
            case BREAK:
                return new BreakEntity((CtBreak) statement);
            case CONTINUE:
                return new ContinueEntity((CtContinue) statement);
            case ASSERT:
                return new AssertEntity((CtAssert<?>) statement);
            case SYNCHRONIZED:
                return new SynchronizedEntity((CtSynchronized) statement);
            case LOCAL_VARIABLE_DECLARATION:
                return new LocalVariableDeclarationEntity((CtLocalVariable<?>) statement);
            case CLASS_DECLARATION:
                return new ClassDeclarationStatement((CtClass<?>) statement);
            case EXPRESSION_STATEMENT:
                return new ExpressionStatementEntity(statement);
        }

        return new StatementEntity<>(statement);
    }

    public StatementExpressionListEntity wrap(List<CtStatement> statements) {
        Function<? super CtStatement, Entity<?>> statementExpressionWrapper = statement -> {
            if (statement instanceof CtExpression) {
                return wrap((CtExpression<?>) statement);
            }
            return wrap(statement);
        };

        List<Entity<?>> list = statements.stream()
                .map(statementExpressionWrapper)
                .collect(Collectors.toCollection(ArrayList::new));

        return new StatementExpressionListEntity(list);
    }

    public CatchEntity wrap(CtCatch catcher) {
        return new CatchEntity(catcher);
    }

    public ExpressionEntity<?> wrap(CtExpression<?> expression) {
        if (!CodeOntology.processExpressions()) {
            return new ExpressionEntity<>(expression);
        }

        if (expression instanceof CtAssignment) {
            return new AssignmentExpressionEntity((CtAssignment) expression);
        } else if (expression instanceof CtConstructorCall<?>) {
            return new ClassInstanceCreationExpression((CtConstructorCall<?>) expression);
        } else if (expression instanceof CtInvocation) {
            return new MethodInvocationExpressionEntity((CtInvocation<?>) expression);
        }

        return new ExpressionEntity<>(expression);
    }

    public Entity<?> wrap(CtVariable<?> variable) {

        if (variable instanceof CtField<?>) {
            return new FieldEntity((CtField) variable);
        }

        if (variable instanceof CtLocalVariable<?>){
            return new LocalVariableEntity((CtLocalVariable<?>) variable);
        }

        return null;
    }

    public SwitchLabelEntity wrap(CtCase<?> label) {
        if (label.getCaseExpression() != null) {
            return new CaseLabelEntity(label);
        }

        return new DefaultLabelEntity(label);
    }

}