package org.codeontology.extraction;

import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WrapperFactory {

    private Factory parent;
    private static WrapperFactory instance;
    private Set<TypeVariable> previousVariables;

    private WrapperFactory() {
        previousVariables = new HashSet<>();
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
            case PARAMETERIZED_TYPE:
                wrapper = new ParameterizedTypeWrapper(reference);
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
        CtTypeParameterReference reference = createTypeVariableReference(typeVariable);
        return new TypeVariableWrapper(reference);
    }

    public ArrayWrapper wrap(GenericArrayType array) {
        return new ArrayWrapper(createGenericArrayReference(array));
    }

    public ParameterizedTypeWrapper wrap(ParameterizedType parameterizedType) {
        return new ParameterizedTypeWrapper(createParameterizedTypeReference(parameterizedType));
    }

    public TypeWrapper<?> wrap(Type type) {
        return wrap(createTypeReference(type));
    }

    private CtTypeReference<?> createParameterizedTypeReference(ParameterizedType parameterizedType) {

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Type rawType = parameterizedType.getRawType();
        CtTypeReference<?> reference;

        if (rawType instanceof Class) {
            reference = getParent().Type().createReference((Class) rawType);
        } else {
            reference = getParent().Type().createReference(rawType.getTypeName());
        }

        for (Type actualArgument : actualTypeArguments) {
            reference.addActualTypeArgument(createTypeReference(actualArgument));
        }

        return reference;
    }

    private CtTypeReference createTypeReference(Type type) {
        CtTypeReference reference;
        if (type instanceof ParameterizedType) {
            reference = createParameterizedTypeReference((ParameterizedType) type);
        } else if (type instanceof TypeVariable) {
            reference = createTypeVariableReference((TypeVariable) type);
        } else if (type instanceof GenericArrayType) {
            reference = createGenericArrayReference((GenericArrayType) type);
        } else if (type instanceof Class) {
            reference = getParent().Type().createReference((Class) type);
        } else if (type instanceof WildcardType) {
            reference = createWildcardReference((WildcardType) type);
        } else {
            reference = getParent().Type().createReference(type.getTypeName());
        }

        return reference;
    }

    private CtTypeReference createGenericArrayReference(GenericArrayType array) {
        Type type = array;

        int i = 0;
        do {
            i++;
            type = ((GenericArrayType) type).getGenericComponentType();
        } while (type instanceof GenericArrayType);

        CtTypeReference<?> componentType;

        if (type instanceof TypeVariable) {
            componentType = createTypeVariableReference((TypeVariable) type);
        } else {
            componentType = createParameterizedTypeReference((ParameterizedType) type);
        }
        return getParent().Type().createArrayReference(componentType, i);
    }

    private CtTypeParameterReference createTypeVariableReference(TypeVariable typeVariable) {
        if (previousVariables.contains(typeVariable)) {
            return getParent().Type().createTypeParameterReference(typeVariable.getName());
        }

        previousVariables.add(typeVariable);

        String name = typeVariable.getName();
        Type[] bounds = typeVariable.getBounds();

        List<CtTypeReference<?>> boundsList = new ArrayList<>();

        for (Type bound : bounds) {
            boundsList.add(createTypeReference(bound));
        }

        previousVariables.remove(typeVariable);

        return getParent().Type().createTypeParameterReference(name, boundsList);
    }

    private CtTypeParameterReference createWildcardReference(WildcardType wildcard) {
        String name = "?";
        Type[] upperBounds = wildcard.getUpperBounds();
        Type[] lowerBounds = wildcard.getLowerBounds();

        List<CtTypeReference<?>> boundsList = new ArrayList<>();

        for (Type bound : upperBounds) {
            boundsList.add(createTypeReference(bound));
        }

        for (Type bound: lowerBounds) {
            boundsList.add(createTypeReference(bound));
        }

        CtTypeParameterReference wildcardReference = getParent().Type().createTypeParameterReference(name, boundsList);
        wildcardReference.setUpper(upperBounds.length > 0);

        return wildcardReference;
    }

    public void setParent(Factory parent) {
        this.parent = parent;
    }

    public Factory getParent() {
        return parent;
    }

}
