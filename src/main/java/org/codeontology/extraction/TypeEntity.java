package org.codeontology.extraction;

import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

public enum TypeEntity {
    CLASS,
    INTERFACE,
    ANNOTATION,
    ENUM,
    PRIMITIVE,
    ARRAY,
    GENERIC;

    public static TypeEntity getEntity(CtTypeReference<?> reference) {

        if (reference instanceof CtArrayTypeReference<?>) {
            return ARRAY;
        }

        CtType<?> type = reference.getDeclaration();
        if (type != null) {
            return getEntity(type);
        }

        if (reference.isPrimitive()) {
            return PRIMITIVE;
        }

        try {
            Class actualClass = reference.getActualClass();
            if (actualClass.isAnnotation()) {
                return ANNOTATION;
            } else if (actualClass.isEnum()) {
                return ENUM;
            } else if (actualClass.isInterface()) {
                return INTERFACE;
            } else {
                if (reference.getPackage() != null) {
                    return CLASS;
                } else {
                    return GENERIC;
                }
            }
        } catch (SpoonClassNotFoundException e) {
            return null;
        }

    }

    public static TypeEntity getEntity(CtType<?> type) {
        if (type.getReference() instanceof CtArrayTypeReference) {
            return ARRAY;
        } else if (type.isPrimitive()) {
            return PRIMITIVE;
        }  else if (type instanceof CtAnnotationType<?>) {
            return ANNOTATION;
        } else if (type instanceof CtEnum<?>) {
            return ENUM;
        } else if (type instanceof CtInterface<?>) {
            return INTERFACE;
        } else {
            return CLASS;
        }
    }
}
