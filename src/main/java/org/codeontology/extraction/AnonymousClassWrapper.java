package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;

import java.util.Set;

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
        tagSuperType();
        tagComment();
        tagFields();
        tagMethods();
        tagSourceCode();
        tagNestedTypes();
    }

    public void tagSuperType() {
        Set<CtTypeReference<?>> references = getReference().getSuperInterfaces();
        if (references.size() > 1) {
            throw new RuntimeException("size > 1");
        }
    }


}
