package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.reference.CtTypeReference;

public class EnumEntity<T extends Enum<?>> extends ClassEntity<T> {

    public EnumEntity(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ENUM_ENTITY;
    }
}
