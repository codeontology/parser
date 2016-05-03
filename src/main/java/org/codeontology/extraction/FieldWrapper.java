package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.*;
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
        if (isDeclarationAvailable()) {
            return getFactory().wrap(getElement().getType());
        } else {
            TypeWrapper<?> type = getGenericType();
            if (type == null) {
                CtTypeReference<?> typeReference = ((CtFieldReference<?>) getReference()).getType();
                type = getFactory().wrap(typeReference);
            }
            type.setParent(getDeclaringElement());
            return type;
        }
    }

    private TypeWrapper<?> getGenericType() {
        TypeWrapper<?> result = null;
        if (!isDeclarationAvailable()) {
            try {
                CtFieldReference<?> reference = ((CtFieldReference<?>) getReference());
                Field field = (Field) reference.getActualField();
                Type genericType = field.getGenericType();

                if (genericType instanceof GenericArrayType ||
                        genericType instanceof TypeVariable<?>  ||
                        genericType instanceof ParameterizedType) {

                    result = getFactory().wrap(genericType);
                }

            } catch (Throwable t) {
                return null;
            }
        }

        return result;
    }

    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType(getDeclaringElement());
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