package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class PrimitiveTypeEntity extends TypeEntity<CtType<?>> {

    public PrimitiveTypeEntity(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PRIMITIVE_ENTITY;
    }

    @Override
    public String buildRelativeURI() {
        String uri =  super.buildRelativeURI();
        return uri.substring(0, 1).toUpperCase() + uri.substring(1);
    }

    @Override
    public void extract() {

    }
}
