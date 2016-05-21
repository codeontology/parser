package org.codeontology.extraction.support;

import org.codeontology.extraction.Entity;
import org.codeontology.extraction.declaration.TypeEntity;
import spoon.reflect.declaration.CtTypedElement;

public interface TypedElementEntity<T extends CtTypedElement> extends Entity<T> {

    TypeEntity<?> getJavaType();

    void tagJavaType();

}