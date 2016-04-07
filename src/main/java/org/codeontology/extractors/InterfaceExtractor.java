package org.codeontology.extractors;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.reference.CtTypeReference;

public class InterfaceExtractor extends TypeExtractor<CtInterface<?>> {

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
