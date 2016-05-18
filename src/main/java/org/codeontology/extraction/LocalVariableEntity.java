package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtTypeReference;

public class LocalVariableEntity extends NamedElementEntity<CtLocalVariable<?>> implements MemberEntity<CtLocalVariable<?>>, TypedElementEntity<CtLocalVariable<?>> {

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
        entity.setParent(getExecutableParent());
        return entity;
    }

    private Entity<?> getExecutableParent() {
        Entity<?> parent = getParent();
        while (parent != null && !(parent instanceof ExecutableEntity<?>)) {
            parent = parent.getParent();
        }

        return parent;
    }

    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType();
    }
}
