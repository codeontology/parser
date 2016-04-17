package org.codeontology.extraction;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.reference.CtTypeReference;

public class EnumWrapper<T extends Enum<?>> extends ClassWrapper<T> {

    public EnumWrapper(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ENUM_CLASS;
    }
}
