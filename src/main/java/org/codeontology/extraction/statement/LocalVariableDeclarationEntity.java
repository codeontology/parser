package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.declaration.LocalVariableEntity;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.ExpressionHolderEntity;
import org.codeontology.extraction.support.ExpressionTagger;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;

public class LocalVariableDeclarationEntity extends StatementEntity<CtLocalVariable<?>>
        implements ExpressionHolderEntity<CtLocalVariable<?>> {

    public LocalVariableDeclarationEntity(CtLocalVariable<?> element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.LOCAL_VARIABLE_DECLARATION_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagVariable();
        tagInitializer();
    }

    public void tagVariable() {
        LocalVariableEntity variable = getVariable();
        getLogger().addTriple(variable, Ontology.DECLARATION_PROPERTY, this);
        variable.extract();
    }

    public LocalVariableEntity getVariable() {
        LocalVariableEntity variable = getFactory().wrap(getElement());
        variable.setParent(this);
        return variable;
    }

    public void tagInitializer() {
        tagExpression();
    }

    @Override
    public ExpressionEntity<?> getExpression() {
        CtExpression<?> defaultExpression = getElement().getDefaultExpression();
        if (defaultExpression != null) {
            ExpressionEntity<?> initializer = getFactory().wrap(defaultExpression);
            initializer.setParent(this);
            return initializer;
        }

        return null;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression(Ontology.INITIALIZER_PROPERTY);
    }
}
