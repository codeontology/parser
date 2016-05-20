package org.codeontology.extraction;

import spoon.reflect.declaration.CtNamedElement;

public interface MemberEntity<T extends CtNamedElement> extends Entity<T> {

    Entity<?> getDeclaringElement();

    void tagDeclaringElement();
}
