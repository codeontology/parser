package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.LineTagger;
import org.codeontology.extraction.support.VariableDeclarationEntity;
import org.codeontology.extraction.support.VariableDeclarationTagger;
import spoon.reflect.declaration.CtField;

public class FieldDeclaration extends CodeElementEntity<CtField<?>> implements VariableDeclarationEntity<CtField<?>> {

    public FieldDeclaration(CtField<?> element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return null;
    }

    @Override
    public void extract() {
        tagType();
        tagLine();
        tagVariable();
        tagInitializer();
        tagSourceCode();
    }

    @Override
    public ExpressionEntity<?> getInitializer() {
        return VariableDeclarationTagger.initializerOf(this);
    }

    @Override
    public void tagInitializer() {
        new VariableDeclarationTagger(this).tagInitializer();
    }

    @Override
    public Entity<?> getVariable() {
        return VariableDeclarationTagger.declaredVariableOf(this);
    }

    @Override
    public void tagVariable() {
        getLogger().addTriple(getParent(), Ontology.DECLARATION_PROPERTY, this);
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }
}
