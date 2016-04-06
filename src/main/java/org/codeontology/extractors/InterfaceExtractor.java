package org.codeontology.extractors;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Set;

public class InterfaceExtractor extends TypeExtractor<CtInterface<?>> {
    public InterfaceExtractor(CtInterface<?> clazz) {
        super(clazz);
    }

    public InterfaceExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.getInterfaceIndividual();
    }

    @Override
    public void extract() {
        tagInterface();
        writeRDF();
    }

    protected void tagInterface() {
        tagType();
        tagName();
        if (isDeclarationAvailable()) {
            tagSuperInterfaces();
            tagFields();
            tagMethods();
            tagSourceCode();
            tagComment();
        }
    }
}
