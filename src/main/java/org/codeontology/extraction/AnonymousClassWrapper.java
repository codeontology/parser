package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtClass;

public class AnonymousClassWrapper<T> extends ClassWrapper<T> {

    public static final String TAG = "anonymous-class";

    public AnonymousClassWrapper(CtClass<T> anonymousClass) {
        super(anonymousClass);
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    public RDFNode getType() {
        return Ontology.ANONYMOUS_CLASS_ENTITY;
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
    }


}
