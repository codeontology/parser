package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtField;


public class FieldWrapper extends TypeMemberWrapper<CtField<?>> {

    JavaTypeTagger tagger;

    public FieldWrapper(CtField<?> field) {
        super(field);
        tagger = new JavaTypeTagger(this);
    }

    @Override
    protected String getRelativeURI() {
        return getFactory().wrap(getElement().getDeclaringType()).getRelativeURI() + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.FIELD_CLASS;
    }

    @Override
    public void extract() {
        tagName();
        tagType();
        tagComment();
        tagJavaType();
        tagVisibility();
        tagModifier();
        tagDeclaringType();
        tagAnnotations();
    }

    protected void tagJavaType() {
        tagger.tagJavaType(getElement().getDeclaringType());
    }
}

