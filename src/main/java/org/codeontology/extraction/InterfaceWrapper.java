package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
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
        if (isDeclarationAvailable() || CodeOntology.isJarExplorationEnabled()) {
            tagSuperInterfaces();
            tagFields();
            tagMethods();
            tagModifiers();
        }
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagSourceCode();
            tagComment();
        }
    }

    public void tagSuperInterfaces() {
        tagSuperInterfaces(Ontology.EXTENDS_PROPERTY);
    }
}
