package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtField;


public class FieldExtractor extends TypeMemberExtractor<CtField<?>> {
    public FieldExtractor(CtField<?> field) {
        super(field);
    }

    @Override
    protected String getRelativeURI() {
        return getFactory().getExtractor(getElement().getDeclaringType()).getRelativeURI() + SEPARATOR + getElement().getSimpleName();
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
        // todo: tagDefaultValue();
    }
}

