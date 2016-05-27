package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.statement.FieldDeclaration;
import org.codeontology.extraction.support.*;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class FieldEntity extends NamedElementEntity<CtField<?>>
        implements ModifiableEntity<CtField<?>>, MemberEntity<CtField<?>>, TypedElementEntity<CtField<?>> {

    public FieldEntity(CtField<?> field) {
        super(field);
    }

    public FieldEntity(CtFieldReference<?> field) {
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
            tagSourceCode();
            tagComment();
            tagAnnotations();
            if (CodeOntology.processStatements()) {
                tagDeclaration();
            }
        }
    }

    @Override
    public List<org.codeontology.extraction.support.Modifier> getModifiers() {
        if (isDeclarationAvailable()) {
            return Modifier.asList(getElement().getModifiers());
        }
        try {
            return Modifier.asList(((CtFieldReference<?>) getReference()).getModifiers());
        } catch (Exception | Error e) {
            return new ArrayList<>();
        }
    }

    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    @Override
    public TypeEntity<?> getJavaType() {
        TypeEntity<?> type;
        if (isDeclarationAvailable()) {
            type = getFactory().wrap(getElement().getType());
        } else {
            type = getGenericType();
            if (type == null) {
                CtTypeReference<?> typeReference = ((CtFieldReference<?>) getReference()).getType();
                type = getFactory().wrap(typeReference);
            }
        }

        type.setParent(getDeclaringElement());
        return type;
    }

    private TypeEntity<?> getGenericType() {
        TypeEntity<?> result = null;
        if (isDeclarationAvailable()) {
            return null;
        }
        try {
            CtFieldReference<?> reference = ((CtFieldReference<?>) getReference());
            Field field = (Field) reference.getActualField();
            Type genericType = field.getGenericType();

            if (genericType instanceof GenericArrayType ||
                    genericType instanceof TypeVariable<?>) {

                result = getFactory().wrap(genericType);
            }

        } catch (Throwable t) {
            return null;
        }

        return result;
    }

    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType();
    }

    @Override
    public Entity<?> getDeclaringElement() {
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

    public void tagDeclaration() {
        FieldDeclaration declaration = new FieldDeclaration(getElement());
        declaration.setParent(this);
        declaration.extract();
    }
}