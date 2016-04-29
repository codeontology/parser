package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtLambda;

public class LambdaWrapper extends Wrapper<CtLambda<?>> {
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
        Wrapper<?> implementedType = getFactory().wrap(getElement().getType());
        implementedType.setParent(this.getParent());
        getLogger().addTriple(this, Ontology.IMPLEMENTS_PROPERTY, implementedType);
        implementedType.follow();
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + getElement().getSimpleName() + SEPARATOR +
                getElement().getType().getQualifiedName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.LAMBDA_ENTITY;
    }
}
