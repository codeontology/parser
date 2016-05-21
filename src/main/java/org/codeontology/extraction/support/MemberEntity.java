package org.codeontology.extraction.support;

import org.codeontology.extraction.Entity;
import spoon.reflect.declaration.CtNamedElement;

public interface MemberEntity<T extends CtNamedElement> extends Entity<T> {

    Entity<?> getDeclaringElement();

    void tagDeclaringElement();
}
