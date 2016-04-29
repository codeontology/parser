package org.codeontology.extraction;

import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;

import java.util.List;

public interface ModifiableWrapper<T extends CtModifiable & CtNamedElement> extends Wrapper<T> {

    List<Modifier> getModifiers();

    void tagModifiers();

}
