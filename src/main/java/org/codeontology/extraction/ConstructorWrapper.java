package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.reference.CtExecutableReference;

public class ConstructorWrapper extends ExecutableWrapper<CtConstructor<?>> {

    public ConstructorWrapper(CtConstructor<?> constructor) {
        super(constructor);
    }

    public ConstructorWrapper(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CONSTRUCTOR_CLASS;
    }
}
