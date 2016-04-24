package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.reference.CtTypeReference;

public class InterfaceWrapper extends TypeWrapper<CtInterface<?>> {

    public InterfaceWrapper(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.INTERFACE_CLASS;
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagSuperInterfaces();
            tagFields();
            tagMethods();
            tagSourceCode();
            tagComment();
            tagModifiers();
        }
    }

    public void tagSuperInterfaces() {
        tagSuperInterfaces(Ontology.EXTENDS_PROPERTY);
    }

    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }
}
