package org.codeontology.extraction.expression;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtConstructorCall;

public class ClassInstanceCreationExpression extends AbstractInvocationExpressionEntity<CtConstructorCall<?>> {
    public ClassInstanceCreationExpression(CtConstructorCall<?> expression) {
        super(expression);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CLASS_INSTANCE_CREATION_EXPRESSION_ENTITY;
    }
}
