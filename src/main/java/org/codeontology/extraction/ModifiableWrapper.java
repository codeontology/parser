package org.codeontology.extraction;

import spoon.reflect.declaration.CtModifiable;

import java.util.List;

public interface ModifiableWrapper<T extends CtModifiable> extends Wrapper<T> {

    List<Modifier> getModifiers();

    void tagModifiers();

}
