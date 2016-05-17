package org.codeontology.extraction;

import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

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

        TypeKind kind = TypeKind.getKind(reference);
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
                entity = new ParameterizedTypeEntity(reference);
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

    private ReflectionFactory reflectionFactory() {
        return ReflectionFactory.getInstance();
    }



    /************************************************************
     *                                                          *
     *              STATEMENTS AND EXPRESSIONS                  *
     *                                                          *
     ************************************************************/

    public StatementEntity<?> wrap(CtStatement statement) {

        if (statement instanceof CtIf) {
            return new IfThenElseEntity((CtIf) statement);
        }

        return new StatementEntity<>(statement);
    }

    public ExpressionEntity wrap(CtExpression<?> expression) {
        return new ExpressionEntity(expression);
    }

}
