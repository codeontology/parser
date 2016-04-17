package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class PrimitiveTypeWrapper extends TypeWrapper<CtType<?>> {

    public PrimitiveTypeWrapper(CtTypeReference<?> reference) {
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
