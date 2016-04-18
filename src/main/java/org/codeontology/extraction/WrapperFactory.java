package org.codeontology.extraction;

import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class WrapperFactory {

    private Factory parent;
    private static WrapperFactory instance;

    private WrapperFactory() {

    }

    public static WrapperFactory getInstance() {
        if (instance == null) {
            instance = new WrapperFactory();
        }
        return instance;
    }

    public PackageWrapper wrap(CtPackage pack) {
        return new PackageWrapper(pack);
    }

    public FieldWrapper wrap(CtField<?> field) {
        return new FieldWrapper(field);
    }

    public MethodWrapper wrap(CtMethod<?> method) {
        return new MethodWrapper(method);
    }

    public TypeWrapper wrap(CtType<?> type) {
        return wrap(type.getReference());
    }

    public TypeWrapper wrap(CtTypeReference<?> reference) {
        TypeWrapper<?> wrapper = null;

        if (reference.getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            return null;
        }

        TypeEntity entity = TypeEntity.getEntity(reference);
        if (entity == null) {
            return null;
        }

        switch (entity) {
            case CLASS:
                wrapper = new ClassWrapper<>(reference);
                break;
            case INTERFACE:
                wrapper = new InterfaceWrapper(reference);
                break;
            case ENUM:
                wrapper = new EnumWrapper<>(reference);
                break;
            case ANNOTATION:
                wrapper = new AnnotationWrapper(reference);
                break;
            case PRIMITIVE:
                wrapper = new PrimitiveTypeWrapper(reference);
                break;
            case ARRAY:
                wrapper = new ArrayWrapper(reference);
                break;
            case TYPE_VARIABLE:
                wrapper = new TypeVariableWrapper(reference);
                break;
        }
        return wrapper;
    }

    public LocalVariableWrapper wrap(CtLocalVariable<?> variable) {
        return new LocalVariableWrapper(variable);
    }

    public ParameterWrapper wrap(CtParameter<?> parameter) {
        try {
            return new ParameterWrapper(parameter);
        } catch (NullTypeException e) {
            return null;
        }
    }

    public ParameterWrapper wrapByTypeReference(CtTypeReference<?> reference) {
        return new ParameterWrapper(reference);
    }

    public ConstructorWrapper wrap(CtConstructor<?> constructor) {
        return new ConstructorWrapper(constructor);
    }

    public ExecutableWrapper<?> wrap(CtExecutableReference<?> reference) {
        if (reference.isConstructor()) {
            return new ConstructorWrapper(reference);
        } else {
            return new MethodWrapper(reference);
        }
    }

    public LambdaWrapper wrap(CtLambda<?> lambda) {
        return new LambdaWrapper(lambda);
    }

    public TypeVariableWrapper wrap(TypeVariable typeVariable) {
        CtTypeParameterReference reference = createTypeParameterReference(typeVariable);
        return new TypeVariableWrapper(reference);
    }

    public ArrayWrapper wrap(GenericArrayType array) {
        Type type = array;

        int i = 0;
        do {
            i++;
            type = ((GenericArrayType) type).getGenericComponentType();
        } while (type instanceof GenericArrayType);

        CtTypeParameterReference typeVariable = createTypeParameterReference((TypeVariable) type);
        CtArrayTypeReference arrayReference = getParent().Type().createArrayReference(typeVariable, i);
        return new ArrayWrapper(arrayReference);
    }

    private CtTypeParameterReference createTypeParameterReference(TypeVariable typeVariable) {
        String name = typeVariable.getName();
        Type[] bounds = typeVariable.getBounds();

        List<CtTypeReference<?>> boundsList = new ArrayList<>();

        for (Type bound : bounds) {
            boundsList.add(getParent().Type().createReference(bound.getTypeName()));
        }

        return getParent().Type().createTypeParameterReference(name, boundsList);
    }

    public void setParent(Factory parent) {
        this.parent = parent;
    }

    public Factory getParent() {
        return parent;
    }

}
