package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;


public class FieldWrapper extends Wrapper<CtField<?>> implements ModifiableWrapper {

    public FieldWrapper(CtField<?> field) {
        super(field);
    }

    public FieldWrapper(CtFieldReference<?> field) {
        super(field);
    }

    @Override
    public String buildRelativeURI() {
        CtTypeReference<?> reference = ((CtFieldReference) getReference()).getDeclaringType();
        TypeWrapper<?> declaringType = getFactory().wrap(reference);
        return declaringType.getRelativeURI() + SEPARATOR + getReference().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.FIELD_ENTITY;
    }

    @Override
    public void extract() {
        tagName();
        tagType();
        tagDeclaringType();
        tagJavaType();
        tagModifiers();
        if (isDeclarationAvailable()) {
            tagComment();
            tagAnnotations();
        }
    }

    public void tagDeclaringType() {
        new DeclaredByTagger(this).tagDeclaredBy();
    }

    @Override
    public List<Modifier> getModifiers() {
        if (isDeclarationAvailable()) {
            return Modifier.asList(getElement().getModifiers());
        } else {
            return Modifier.asList(((CtFieldReference<?>) getReference()).getModifiers());
        }
    }

    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    public void tagJavaType() {
        CtTypeReference<?> declaringType = ((CtFieldReference<?>) getReference()).getDeclaringType();
        Wrapper<?> parent = getFactory().wrap(declaringType);
        new JavaTypeTagger(this).tagJavaType(parent);
    }
}

