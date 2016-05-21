package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.declaration.ExecutableEntity;
import org.codeontology.extraction.declaration.TypeEntity;
import org.codeontology.extraction.expression.ExpressionEntity;
import org.codeontology.extraction.support.*;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public class LocalVariableDeclarationEntity extends StatementEntity<CtLocalVariable<?>>
        implements TypedElementEntity<CtLocalVariable<?>>, ModifiableEntity<CtLocalVariable<?>>, ExpressionHolderEntity<CtLocalVariable<?>> {

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
        tagJavaType();
        tagModifiers();
        tagName();
        tagInitializer();
    }

    public String getName() {
        return getElement().getSimpleName();
    }

    public void tagName() {
        Literal name = getModel().createTypedLiteral(getName());
        getLogger().addTriple(this, Ontology.NAME_PROPERTY, name);
    }

    @Override
    public TypeEntity<?> getJavaType() {
        CtTypeReference<?> type = getElement().getType();
        TypeEntity<?> entity = getFactory().wrap(type);
        entity.setParent(getParent(ExecutableEntity.class, TypeEntity.class));
        return entity;
    }

    @Override
    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType();
    }

    @Override
    public List<Modifier> getModifiers() {
        return Modifier.asList(getElement().getModifiers());
    }

    @Override
    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    public void tagInitializer() {
        tagExpression();
    }

    @Override
    public ExpressionEntity getExpression() {
        ExpressionEntity initializer = getFactory().wrap(getElement().getDefaultExpression());
        initializer.setParent(this);
        return initializer;
    }

    @Override
    public void tagExpression() {
        new ExpressionTagger(this).tagExpression(Ontology.INITIALIZER_PROPERTY);
    }
}
