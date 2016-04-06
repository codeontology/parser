package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class PrimitiveTypeExtractor extends TypeExtractor<CtType<?>> {
    public PrimitiveTypeExtractor(CtType<?> type) {
        super(type);
    }

    public PrimitiveTypeExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.getPrimitiveIndividual();
    }

    @Override
    public void extract() {

    }
}
