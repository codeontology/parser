package org.codeontology.extraction;

import spoon.reflect.declaration.CtGenericElement;

import java.util.List;

public interface GenericDeclarationEntity<T extends CtGenericElement> extends Entity<T> {

    List<TypeVariableEntity> getFormalTypeParameters();

    void tagFormalTypeParameters();

}
