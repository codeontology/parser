package org.codeontology.extraction.expression;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.support.LineTagger;
import spoon.reflect.code.CtExpression;

public class ExpressionEntity extends CodeElementEntity<CtExpression<?>> {

    public ExpressionEntity(CtExpression<?> expression) {
        super(expression);
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
