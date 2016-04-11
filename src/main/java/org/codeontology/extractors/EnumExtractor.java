package org.codeontology.extractors;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.reference.CtTypeReference;

public class EnumExtractor<T extends Enum<?>> extends ClassExtractor<T> {

    public EnumExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ENUM_CLASS;
    }
}
