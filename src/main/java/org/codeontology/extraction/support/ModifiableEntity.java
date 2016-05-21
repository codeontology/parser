package org.codeontology.extraction.support;

import org.codeontology.extraction.Entity;
import spoon.reflect.declaration.CtModifiable;

import java.util.List;

public interface ModifiableEntity<T extends CtModifiable> extends Entity<T> {

    List<Modifier> getModifiers();

    void tagModifiers();

}
