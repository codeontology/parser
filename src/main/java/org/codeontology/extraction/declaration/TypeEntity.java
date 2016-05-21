package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.Property;
import org.codeontology.CodeOntology;
import org.codeontology.exceptions.NullTypeException;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.support.ModifiableEntity;
import org.codeontology.extraction.support.ModifiableTagger;
import org.codeontology.extraction.support.Modifier;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class TypeEntity<T extends CtType<?>> extends NamedElementEntity<T> implements ModifiableEntity<T> {

    private List<MethodEntity> methods;
    private List<FieldEntity> fields;

    public TypeEntity(T type) {
        super(type);
        checkNullType();
    }

    public TypeEntity(CtTypeReference<?> reference) {
        super(reference);
        checkNullType();
    }

    private void checkNullType() {
        if (getReference().getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            throw new NullTypeException();
        }
    }

    @Override
    public String buildRelativeURI() {
        return getReference().getQualifiedName();
    }

    public CtTypeReference<?> getReference() {
        return (CtTypeReference<?>) super.getReference();
    }

    public void tagSuperInterfaces(Property property) {
        Set<CtTypeReference<?>> references = getReference().getSuperInterfaces();

        for (CtTypeReference<?> reference : references) {
            TypeEntity<?> superInterface = getFactory().wrap(reference);
            superInterface.setParent(this);
            getLogger().addTriple(this, property, superInterface.getResource());
            superInterface.follow();
        }
    }

    public void tagMethods() {
        List<MethodEntity> methods = getMethods();
        for (MethodEntity method : methods) {
            method.extract();
        }
    }

    public List<MethodEntity> getMethods() {
        if (methods == null) {
            setMethods();
        }
        return methods;
    }

    private void setMethods() {
        methods = new ArrayList<>();

        if (isDeclarationAvailable()) {
            Set<CtMethod<?>> ctMethods = getElement().getMethods();
            for (CtMethod ctMethod : ctMethods) {
                MethodEntity method = getFactory().wrap(ctMethod);
                method.setParent(this);
                methods.add(method);
            }
        } else {
            setMethodsByReflection();
        }
    }

    private void setMethodsByReflection() {
        try {
            Method[] actualMethods = getReference().getActualClass().getDeclaredMethods();
            for (Method actualMethod : actualMethods) {
                CtExecutableReference<?> reference = ReflectionFactory.getInstance().createMethod(actualMethod);
                MethodEntity method = (MethodEntity) getFactory().wrap(reference);
                method.setParent(this);
                methods.add(method);
            }
        } catch (Throwable t) {
            showMemberAccessWarning();
        }
    }

    public List<FieldEntity> getFields() {
        if (fields == null) {
            setFields();
        }

        return fields;
    }

    private void setFields() {
        fields = new ArrayList<>();
        if (isDeclarationAvailable()) {
            List<CtField<?>> ctFields = getElement().getFields();
            for (CtField<?> current : ctFields) {
                FieldEntity currentField = getFactory().wrap(current);
                currentField.setParent(this);
                fields.add(currentField);
            }
        } else {
            setFieldsByReflection();
        }
    }

    private void setFieldsByReflection() {
        try {
            Field[] actualFields = getReference().getActualClass().getFields();
            for (Field current : actualFields) {
                CtFieldReference<?> reference = ReflectionFactory.getInstance().createField(current);
                FieldEntity currentField = getFactory().wrap(reference);
                currentField.setParent(this);
                fields.add(currentField);
            }
        } catch (Throwable t) {
            showMemberAccessWarning();
        }
    }

    public void tagFields() {
        List<FieldEntity> fields = getFields();

        for (FieldEntity field : fields) {
            field.extract();
        }
    }

    @Override
    public List<Modifier> getModifiers() {
        if (isDeclarationAvailable()) {
            return Modifier.asList(getElement().getModifiers());
        } else {
            return Modifier.asList(getReference().getActualClass().getModifiers());
        }
    }

    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    protected void showMemberAccessWarning() {
        if (CodeOntology.verboseMode()) {
            System.out.println("[WARNING] Cannot extract members of " + getReference().getQualifiedName());
        }
    }
}
