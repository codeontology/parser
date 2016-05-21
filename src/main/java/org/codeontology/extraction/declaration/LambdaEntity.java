package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.NamedElementEntity;
import spoon.reflect.code.CtLambda;

public class LambdaEntity extends NamedElementEntity<CtLambda<?>> {

    public static final String TAG = "lambda";

    public LambdaEntity(CtLambda<?> lambda) {
        super(lambda);
    }

    @Override
    public void extract() {
        tagType();
        tagSourceCode();
        tagFunctionalImplements();
    }

    private void tagFunctionalImplements() {
        Entity<?> implementedType = getFactory().wrap(getElement().getType());
        implementedType.setParent(this.getParent());
        getLogger().addTriple(this, Ontology.IMPLEMENTS_PROPERTY, implementedType);
        implementedType.follow();
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.LAMBDA_ENTITY;
    }
}
