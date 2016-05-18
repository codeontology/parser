package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtExpression;

public class ExpressionEntity extends CodeElementEntity<CtExpression<?>> {

    public ExpressionEntity(CtExpression<?> expression) {
        super(expression);
    }

    public static final String TAG = "expression";

    @Override
    protected String buildRelativeURI() {
        int line = getElement().getPosition().getLine();
        int column = getElement().getPosition().getColumn();

        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + line + SEPARATOR + column;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.EXPRESSION_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagSourceCode();
        tagLine();
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }
}
