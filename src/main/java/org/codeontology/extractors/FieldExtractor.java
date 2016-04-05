package org.codeontology.extractors;

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
        return Ontology.getFieldClass();
    }

    @Override
    public void extract() {
        tagName();
        tagType();
        tagComment();
        tagJavaType();
        tagEncapsulation();
        tagModifier();
        tagDeclaringType();
        // todo: tagDefaultValue();
    }
}

