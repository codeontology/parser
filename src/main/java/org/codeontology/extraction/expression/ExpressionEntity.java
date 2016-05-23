package org.codeontology.extraction.expression;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.CodeElementEntity;
import org.codeontology.extraction.declaration.TypeEntity;
import org.codeontology.extraction.support.GenericDeclarationEntity;
import org.codeontology.extraction.support.JavaTypeTagger;
import org.codeontology.extraction.support.LineTagger;
import org.codeontology.extraction.support.TypedElementEntity;
import spoon.reflect.code.CtExpression;

public class ExpressionEntity<E extends CtExpression<?>> extends CodeElementEntity<E>
        implements TypedElementEntity<E> {

    public ExpressionEntity(E expression) {
        super(expression);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.EXPRESSION_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagJavaType();
        tagSourceCode();
        tagLine();
    }

    public void tagLine() {
        new LineTagger(this).tagLine();
    }

    @Override
    public TypeEntity<?> getJavaType() {
        TypeEntity<?> type = getFactory().wrap(getElement().getType());
        if (type != null) {
            type.setParent(getParent(GenericDeclarationEntity.class));
            return type;
        }

        return null;
    }

    @Override
    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType();
    }
}
