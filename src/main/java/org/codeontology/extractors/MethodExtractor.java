package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;

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
        return Ontology.getMethodIndividual();
    }

    @Override
    public void extract() {
        super.extract();
        tagReturns();
    }

    protected void tagReturns() {
        Extractor extractor = getFactory().getExtractor(((CtExecutableReference<?>) getReference()).getType());
        addStatement(Ontology.getReturnProperty(), extractor.getResource());
    }
}
