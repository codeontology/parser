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
        return getFactory().wrap(type);
    }

    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType(this.getParent());
    }
}
