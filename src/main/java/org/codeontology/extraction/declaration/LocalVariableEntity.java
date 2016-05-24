package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.support.*;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public class LocalVariableEntity extends NamedElementEntity<CtLocalVariable<?>>
        implements MemberEntity<CtLocalVariable<?>>, TypedElementEntity<CtLocalVariable<?>>, ModifiableEntity<CtLocalVariable<?>> {

    public LocalVariableEntity(CtLocalVariable<?> variable) {
        super(variable);
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagJavaType();
        tagModifiers();
        tagDeclaringElement();
    }

    @Override
    public List<Modifier> getModifiers() {
        return Modifier.asList(getElement().getModifiers());
    }

    @Override
    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.LOCAL_VARIABLE_ENTITY;
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
