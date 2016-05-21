package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.BodyHolderEntity;
import org.codeontology.extraction.support.BodyTagger;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSynchronized;

public class SynchronizedEntity extends StatementEntity<CtSynchronized>
        implements BodyHolderEntity<CtSynchronized>, ExpressionHolderEntity<CtSynchronized> {

    public SynchronizedEntity(CtSynchronized element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.SYNCHRONIZED_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagBody();
        tagExpression();
    }

    @Override
    public StatementEntity<?> getBody() {
        StatementEntity<?> body = getFactory().wrap(getElement().getBlock());
        body.setParent(this);
        return body;
    }

    @Override
    public void tagBody() {
        new BodyTagger(this).tagBody();
    }

    @Override
    public ExpressionEntity getExpression() {
        CtExpression<?> expression = getElement().getExpression();
        if (expression != null) {
            ExpressionEntity entity = getFactory().wrap(expression);
            entity.setParent(this);
            return entity;
        }

        return null;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression();
    }
}
