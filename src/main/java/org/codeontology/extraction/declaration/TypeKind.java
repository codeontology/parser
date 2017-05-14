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

package org.codeontology.extraction.declaration;

import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

public enum TypeKind {
    CLASS,
    INTERFACE,
    ANNOTATION,
    ENUM,
    PRIMITIVE,
    ARRAY,
    TYPE_VARIABLE,
    PARAMETERIZED_TYPE;

    public static TypeKind getKindOf(CtTypeReference<?> reference) {

        if (reference instanceof CtArrayTypeReference<?>) {
            return ARRAY;
        }

        if (reference instanceof CtTypeParameterReference) {
            return TYPE_VARIABLE;
        }

        if (!reference.getActualTypeArguments().isEmpty()) {
            return PARAMETERIZED_TYPE;
        }

        CtType<?> type = reference.getDeclaration();
        if (type != null) {
            return getKindOf(type);
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
                return CLASS;
            }
        } catch (SpoonClassNotFoundException e) {
            return null;
        }

    }

    public static TypeKind getKindOf(CtType type) {

        if (type.getReference() instanceof CtArrayTypeReference) {
            return ARRAY;
        } else if (type.getReference().getActualTypeArguments().size() > 0) {
            return PARAMETERIZED_TYPE;
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