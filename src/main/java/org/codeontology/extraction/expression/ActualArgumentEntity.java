package org.codeontology.extraction.expression;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.AbstractEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;

public class ActualArgumentEntity extends AbstractEntity<ExpressionEntity<?>>
        implements ExpressionHolderEntity<ExpressionEntity<?>> {

    private int position;
    private static final String TAG = "argument";

    public ActualArgumentEntity(ExpressionEntity<?> expression) {
        super(expression);
    }

    @Override
    public String buildRelativeURI() {
        return getElement().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + position;
    }

    @Override
    public void extract() {
        tagType();
        tagPosition();
        tagExpression();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ACTUAL_ARGUMENT_ENTITY;
    }

    @Override
    public ExpressionEntity<?> getExpression() {
        return getElement();
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }

    public void tagPosition() {
        Literal position = getModel().createTypedLiteral(getPosition());
        getLogger().addTriple(this, Ontology.POSITION_PROPERTY, position);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
