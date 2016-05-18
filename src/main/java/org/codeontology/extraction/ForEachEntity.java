package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtForEach;

public class ForEachEntity extends LoopEntity<CtForEach> {

    public ForEachEntity(CtForEach element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.FOR_EACH_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagExpression();
        tagVariable();
    }

    private ExpressionEntity getExpression() {
        ExpressionEntity expression = getFactory().wrap(getElement().getExpression());
        expression.setParent(this);
        return expression;
    }

    public void tagExpression() {
        ExpressionEntity expression = getExpression();
        getLogger().addTriple(this, Ontology.EXPRESSION_PROPERTY, expression);
        expression.extract();
    }

    private LocalVariableEntity getVariable() {
        LocalVariableEntity variable = getFactory().wrap(getElement().getVariable());
        variable.setParent(this);
        return variable;
    }

    public void tagVariable() {
        LocalVariableEntity variable = getVariable();
        getLogger().addTriple(this, Ontology.VARIABLE_PROPERTY, variable);
        variable.extract();
    }
}
