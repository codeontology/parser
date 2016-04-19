package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtLambda;

public class LambdaWrapper extends Wrapper<CtLambda<?>> {
    private ExecutableWrapper<?> parent;
    private static final String TAG = "lambda";

    public LambdaWrapper(CtLambda<?> lambda) {
        super(lambda);
    }

    @Override
    public void extract() {
        tagType();
        tagSourceCode();
        tagFunctionalImplements();
    }

    private void tagFunctionalImplements() {
        Wrapper<?> wrapper = getFactory().wrap(getElement().getType());
        getLogger().addTriple(this, Ontology.IMPLEMENTS_PROPERTY, wrapper.getResource());
        if (wrapper.getReference() == null) {
            wrapper.extract();
        }
    }

    @Override
    protected String getRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR +
                getElement().getType().getQualifiedName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.LAMBDA_CLASS;
    }

    public void setParent(ExecutableWrapper<?> executable) {
        this.parent = executable;
    }

    public ExecutableWrapper<?> getParent() {
        return parent;
    }
}
