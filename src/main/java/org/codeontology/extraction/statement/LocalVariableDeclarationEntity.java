package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.VariableDeclarationEntity;
import org.codeontology.extraction.support.VariableDeclarationTagger;
import spoon.reflect.code.CtLocalVariable;

public class LocalVariableDeclarationEntity extends StatementEntity<CtLocalVariable<?>>
        implements VariableDeclarationEntity<CtLocalVariable<?>> {

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

    public Entity<?> getVariable() {
        return VariableDeclarationTagger.declaredVariableOf(this);
    }

    @Override
    public void tagVariable() {
        new VariableDeclarationTagger(this).tagVariable();
    }

    @Override
    public ExpressionEntity<?> getInitializer() {
        return VariableDeclarationTagger.initializerOf(this);
    }

    public void tagInitializer() {
        new VariableDeclarationTagger(this).tagInitializer();
    }
}
