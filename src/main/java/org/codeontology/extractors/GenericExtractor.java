package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class GenericExtractor extends TypeExtractor<CtType<?>> {

    public GenericExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    public void extract() {
        tagType();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.getGenericIndividual();
    }
}
