package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class FieldWrapper extends AbstractWrapper<CtField<?>> implements ModifiableWrapper<CtField<?>>, MemberWrapper<CtField<?>>, TypedElementWrapper<CtField<?>> {

    public FieldWrapper(CtField<?> field) {
        super(field);
    }

    public FieldWrapper(CtFieldReference<?> field) {
        super(field);
    }

    @Override
    public String buildRelativeURI() {
        return getDeclaringElement().getRelativeURI() + SEPARATOR + getReference().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.FIELD_ENTITY;
    }

    @Override
    public void extract() {
        tagName();
        tagType();
        tagDeclaringElement();
        tagJavaType();
        tagModifiers();
        if (isDeclarationAvailable()) {
            tagComment();
            tagAnnotations();
        }
    }

    @Override
    public List<Modifier> getModifiers() {
        if (isDeclarationAvailable()) {
            return Modifier.asList(getElement().getModifiers());
        } else {
            try {
                return Modifier.asList(((CtFieldReference<?>) getReference()).getModifiers());
            } catch (NoClassDefFoundError e) {
                return new ArrayList<>();
            }
        }
    }

    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    @Override
    public TypeWrapper<?> getJavaType() {
        CtFieldReference<?> reference = (CtFieldReference<?>) getReference();
        return getFactory().wrap(reference.getType());
    }

    public void tagJavaType() {
        CtTypeReference<?> declaringType = ((CtFieldReference<?>) getReference()).getDeclaringType();
        Wrapper<?> parent = getFactory().wrap(declaringType);
        new JavaTypeTagger(this).tagJavaType(parent);
    }

    @Override
    public Wrapper<?> getDeclaringElement() {
        if (isDeclarationAvailable()) {
            return getFactory().wrap(getElement().getDeclaringType());
        } else {
            CtFieldReference<?> reference = (CtFieldReference) getReference();
            CtTypeReference<?> declaringType = ReflectionFactory.getInstance().clone(reference.getDeclaringType());
            declaringType.setActualTypeArguments(new ArrayList<>());
            return getFactory().wrap(declaringType);
        }
    }

    @Override
    public void tagDeclaringElement() {
        new DeclaringElementTagger(this).tagDeclaredBy();
    }
}