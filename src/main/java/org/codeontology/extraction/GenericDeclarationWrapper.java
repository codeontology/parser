package org.codeontology.extraction;

import spoon.reflect.declaration.CtGenericElement;

import java.util.List;

public interface GenericDeclarationWrapper<T extends CtGenericElement> extends Wrapper<T> {

    List<TypeVariableWrapper> getFormalTypeParameters();

    void tagFormalTypeParameters();

}
