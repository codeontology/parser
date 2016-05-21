package org.codeontology.extraction.support;

import org.codeontology.extraction.Entity;
import org.codeontology.extraction.declaration.TypeVariableEntity;
import spoon.reflect.declaration.CtGenericElement;

import java.util.List;

public interface GenericDeclarationEntity<T extends CtGenericElement> extends Entity<T> {

    List<TypeVariableEntity> getFormalTypeParameters();

    void tagFormalTypeParameters();

}
