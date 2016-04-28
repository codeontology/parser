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
        return Ontology.FIELD_CLASS;
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
    public List<ModifierClass> getModifiers() {
        if (isDeclarationAvailable()) {
            return ModifierClass.asList(getElement().getModifiers());
        } else {
            return ModifierClass.asList(((CtFieldReference<?>) getReference()).getModifiers());
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

