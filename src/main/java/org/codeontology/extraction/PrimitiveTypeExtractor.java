package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class PrimitiveTypeExtractor extends TypeExtractor<CtType<?>> {

    public PrimitiveTypeExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PRIMITIVE_CLASS;
    }

    @Override
    public void extract() {

    }
}