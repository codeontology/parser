package org.codeontology.extraction;

import spoon.reflect.declaration.CtClass;

public class AnonymousClassWrapper<T> extends ClassWrapper<T> {

    public static final String TAG = "anonymous-class";

    public AnonymousClassWrapper(CtClass<T> anonymousClass) {
        super(anonymousClass);
    }

    @Override
    public String getRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    public void extract() {
        tagType();
        tagSuperClass();
        tagSuperInterfaces();
        tagComment();
        tagFields();
        tagMethods();
        tagSourceCode();
        tagNestedTypes();
        tagFormalTypeParameters();
    }


}
