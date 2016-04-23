package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtField;


public class FieldWrapper extends Wrapper<CtField<?>> {

    JavaTypeTagger javaTypeTagger;
    private ModifiableTagger modifiableTagger;
    private DeclaredByTagger declaredByTagger;

    public FieldWrapper(CtField<?> field) {
        super(field);

    }

    @Override
    protected String getRelativeURI() {
        return getFactory().wrap(getElement().getDeclaringType()).getRelativeURI() + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.FIELD_CLASS;
    }

    private void setTaggers() {
        javaTypeTagger = new JavaTypeTagger(this);
        modifiableTagger = new ModifiableTagger(this);
        declaredByTagger = new DeclaredByTagger(this);
    }

    @Override
    public void extract() {
        setTaggers();
        tagName();
        tagType();
        tagComment();
        tagJavaType();
        tagVisibility();
        tagModifier();
        tagDeclaringType();
        tagAnnotations();
    }

    protected void tagDeclaringType() {
        declaredByTagger.tagDeclaredBy();
    }

    protected void tagVisibility() {
        modifiableTagger.tagVisibility();
    }

    protected void tagModifier() {
        modifiableTagger.tagModifier();
    }

    protected void tagJavaType() {
        javaTypeTagger.tagJavaType(getElement().getDeclaringType());
    }
}

