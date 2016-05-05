package org.codeontology.extraction;

import spoon.reflect.declaration.CtTypedElement;

public interface TypedElementWrapper<T extends CtTypedElement> extends Wrapper<T> {

    TypeWrapper<?> getJavaType();

    void tagJavaType();

}