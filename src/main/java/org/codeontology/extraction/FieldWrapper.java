package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;


public class FieldWrapper extends Wrapper<CtField<?>> {

    public FieldWrapper(CtField<?> field) {
        super(field);

    }

    @Override
    public String buildRelativeURI() {
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
        tagModifiers();
        tagDeclaringType();
        tagAnnotations();
    }

    public void tagDeclaringType() {
        new DeclaredByTagger(this).tagDeclaredBy();
    }


    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    public void tagJavaType() {
        CtType<?> declaringType = getElement().getDeclaringType();
        Wrapper<?> parent = getFactory().wrap(declaringType);
        new JavaTypeTagger(this).tagJavaType(parent);
    }
}

