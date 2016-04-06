package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtLambda;

public class LambdaExtractor extends Extractor<CtLambda<?>> {
    private ExecutableExtractor<?> parent;
    private static final String TAG = "lambda";

    public LambdaExtractor(CtLambda<?> lambda) {
        super(lambda);
    }

    @Override
    public void extract() {
        tagType();
        tagSourceCode();
        tagFunctionalImplements();
    }

    private void tagFunctionalImplements() {
        Extractor<?> extractor = getFactory().getExtractor(getElement().getType());
        addStatement(Ontology.getLambdaImplementsProperty(), extractor.getResource());
        if (extractor.getReference() == null) {
            extractor.extract();
        }
    }

    @Override
    protected String getRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR +
                getElement().getType().getQualifiedName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.getLambdaIndividual();
    }

    public void setParent(ExecutableExtractor<?> executable) {
        this.parent = executable;
    }

    public ExecutableExtractor<?> getParent() {
        return parent;
    }
}
