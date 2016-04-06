package org.codeontology;

import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtGenericElementReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.CtIntersectionTypeReferenceImpl;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

public enum TypeEntity {
    CLASS,
    INTERFACE,
    ANNOTATION,
    ENUM,
    PRIMITIVE,
    GENERIC;

    public static TypeEntity getEntity(CtTypeReference<?> reference) {

        CtType<?> type = reference.getDeclaration();
        if (type != null) {
            return getEntity(type);
        }

        if (reference.isPrimitive()) {
            return PRIMITIVE;
        }

        if (reference instanceof CtArrayTypeReference<?>) {
            return CLASS;
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
        if (type.isPrimitive()) {
            return PRIMITIVE;
        }  else if (type instanceof CtAnnotationType<?>) {
            return ANNOTATION;
        } else if (type instanceof CtEnum<?>) {
            return  ENUM;
        } else if (type instanceof CtInterface<?>) {
            return INTERFACE;
        } else if (type instanceof CtClass<?>){
            return CLASS;
        } else {
            return null;
        }
    }
}
