package org.codeontology.extraction;

import spoon.reflect.declaration.CtNamedElement;

public interface MemberWrapper<T extends CtNamedElement> extends Wrapper<T> {

    Wrapper<?> getDeclaringElement();

    void tagDeclaringElement();
}
