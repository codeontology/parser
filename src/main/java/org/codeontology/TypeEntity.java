package org.codeontology;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

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

        Class actualClass = reference.getActualClass();
        if (actualClass.isAnnotation()) {
            return ANNOTATION;
        } else if (actualClass.isEnum()) {
            return ENUM;
        } else if (actualClass.isInterface()) {
            return INTERFACE;
        }

        return CLASS;
    }

    public static TypeEntity getEntity(CtType<?> type) {
        if (type.isPrimitive()) {
            return PRIMITIVE;
        }  else if (type instanceof CtAnnotation<?>) {
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
