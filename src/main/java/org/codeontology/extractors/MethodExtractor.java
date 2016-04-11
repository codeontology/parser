package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

public class MethodExtractor extends ExecutableExtractor<CtMethod<?>> {
    public MethodExtractor(CtMethod<?> method) {
        super(method);
    }

    public MethodExtractor(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    protected String getRelativeURI() {
        String uri = getReference().toString();
        uri = uri.replaceAll(", |\\(|\\)", SEPARATOR);
        return uri;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.METHOD_CLASS;
    }

    @Override
    public void extract() {
        super.extract();
        tagReturns();
    }

    protected void tagReturns() {
        CtTypeReference<?> reference = ((CtExecutableReference<?>) getReference()).getType();
        Extractor extractor = getFactory().getExtractor(reference);
        addStatement(Ontology.RETURNS_PROPERTY, extractor.getResource());
        if (reference.getDeclaration() == null) {
            extractor.extract();
        }
    }
}
