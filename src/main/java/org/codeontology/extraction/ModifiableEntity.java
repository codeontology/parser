package org.codeontology.extraction;

import spoon.reflect.declaration.CtModifiable;

import java.util.List;

public interface ModifiableEntity<T extends CtModifiable> extends Entity<T> {

    List<Modifier> getModifiers();

    void tagModifiers();

}
