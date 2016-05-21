package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.reference.CtExecutableReference;

public class ConstructorEntity extends ExecutableEntity<CtConstructor<?>> {

    public ConstructorEntity(CtConstructor<?> constructor) {
        super(constructor);
    }

    public ConstructorEntity(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CONSTRUCTOR_ENTITY;
    }
}
