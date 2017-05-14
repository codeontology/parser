/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.Property;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.support.ModifiableEntity;
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

public abstract class TypeEntity<T extends CtType<?>>
        extends NamedElementEntity<T> implements ModifiableEntity<T> {

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
        methods.forEach(method ->
                getLogger().addTriple(this, Ontology.HAS_METHOD_PROPERTY, method)
        );
        methods.forEach(MethodEntity::extract);
    }

    public List<MethodEntity> getMethods() {
        if (methods == null) {
            setMethods();
        }
        return methods;
    }

    private void setMethods() {
        methods = new ArrayList<>();

        if (!isDeclarationAvailable()) {
            setMethodsByReflection();
            return;
        }

        Set<CtMethod<?>> ctMethods = getElement().getMethods();
        for (CtMethod ctMethod : ctMethods) {
            MethodEntity method = getFactory().wrap(ctMethod);
            method.setParent(this);
            methods.add(method);
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
        if (!isDeclarationAvailable()) {
            setFieldsByReflection();
            return;
        }

        List<CtField<?>> ctFields = getElement().getFields();
        for (CtField<?> current : ctFields) {
            FieldEntity currentField = getFactory().wrap(current);
            currentField.setParent(this);
            fields.add(currentField);
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
        fields.forEach(field -> getLogger().addTriple(this, Ontology.HAS_FIELD_PROPERTY, field));
        fields.forEach(FieldEntity::extract);
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
           CodeOntology.showWarning("Could not extract members of " + getReference().getQualifiedName());
        }
    }
}