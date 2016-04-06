package org.codeontology;

import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

public enum TypeEntity {
    CLASS,
    INTERFACE,
    ANNOTATION,
    ENUM,
    PRIMITIVE;

    public static TypeEntity getEntity(CtTypeReference<?> reference) {

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
            }

            return CLASS;
        } catch (SpoonClassNotFoundException e) {
            return  CLASS;
        }
    }

    public static TypeEntity getEntity(CtType<?> type) {
        if (type.isPrimitive()) {
            return PRIMITIVE;
        }  else if (type instanceof CtAnnotationType<?>) {
            return ANNOTATION;
        } else if (type instanceof CtEnum<?>) {
            return  ENUM;
        } else if (type instanceof CtInterface<?>) {
            return INTERFACE;
        } else {
            return CLASS;
        }
    }
}
