package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.support.DeclaringElementTagger;
import org.codeontology.extraction.support.JavaTypeTagger;
import org.codeontology.extraction.support.MemberEntity;
import org.codeontology.extraction.support.TypedElementEntity;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtTypeReference;

public class LocalVariableEntity extends NamedElementEntity<CtLocalVariable<?>>
        implements MemberEntity<CtLocalVariable<?>>, TypedElementEntity<CtLocalVariable<?>> {

    public LocalVariableEntity(CtLocalVariable<?> variable) {
        super(variable);
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagJavaType();
        tagDeclaringElement();
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.VARIABLE_ENTITY;
    }

    @Override
    public Entity<?> getDeclaringElement() {
        return getParent();
    }

    public void tagDeclaringElement() {
        new DeclaringElementTagger(this).tagDeclaredBy();
    }

    @Override
    public TypeEntity<?> getJavaType() {
        CtTypeReference<?> type = getElement().getType();
        TypeEntity<?> entity = getFactory().wrap(type);
        entity.setParent(getParent(ExecutableEntity.class, TypeEntity.class));
        return entity;
    }

    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType();
    }
}
