package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class TypeVariableExtractor extends TypeExtractor<CtType<?>> {

    public TypeVariableExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    public void extract() {
        tagType();
    }

    @Override
    protected String getRelativeURI() {
        return getReference().getQualifiedName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.GENERIC_CLASS;
    }
}
