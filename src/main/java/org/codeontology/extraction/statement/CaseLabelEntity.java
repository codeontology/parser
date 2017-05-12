package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;

public class CaseLabelEntity extends SwitchLabelEntity implements ExpressionHolderEntity<CtCase<?>> {

    public CaseLabelEntity(CtCase<?> label) {
        super(label);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CASE_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagExpression();
    }

    @Override
    public ExpressionEntity<?> getExpression() {
        CtExpression<?> caseExpression = getElement().getCaseExpression();
        if (caseExpression != null) {
            ExpressionEntity<?> expression = getFactory().wrap(caseExpression);
            expression.setParent(this);
            return expression;
        }

        return null;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }
}
