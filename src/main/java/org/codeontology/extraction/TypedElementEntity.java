package org.codeontology.extraction;

import spoon.reflect.declaration.CtTypedElement;

public interface TypedElementEntity<T extends CtTypedElement> extends Entity<T> {

    TypeEntity<?> getJavaType();

    void tagJavaType();

}