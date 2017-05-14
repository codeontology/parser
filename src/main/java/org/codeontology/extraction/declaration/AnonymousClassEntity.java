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
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnonymousClassEntity<T> extends ClassEntity<T> {

    public static final String TAG = "anonymous-class";
    private Set<Entity<?>> requestedResources;

    public AnonymousClassEntity(CtClass<T> anonymousClass) {
        super(anonymousClass);
        requestedResources = new HashSet<>();
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    public RDFNode getType() {
        return Ontology.ANONYMOUS_CLASS_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagSuperType();
        tagComment();
        tagFields();
        tagMethods();
        tagSourceCode();
        tagNestedTypes();
    }

    public void tagSuperType() {
        Set<CtTypeReference<?>> references = getReference().getSuperInterfaces();
        CtTypeReference<?> superTypeReference;
        Property property;
        if (references.isEmpty()) {
            superTypeReference = getReference().getSuperclass();
            property = Ontology.EXTENDS_PROPERTY;
        } else {
            superTypeReference = (CtTypeReference<?>) references.toArray()[0];
            property = Ontology.IMPLEMENTS_PROPERTY;
        }
        TypeEntity<?> superType = getFactory().wrap(superTypeReference);
        superType.setParent(getParent());
        getLogger().addTriple(this, property, superType);
        requestedResources.add(superType);
        superType.follow();
    }

    public Set<Entity<?>> getRequestedResources() {
        return requestedResources;
    }

    @Override
    public void tagMethods() {
        List<MethodEntity> methods = getMethods();
        for (MethodEntity method : methods) {
            method.extract();
            requestedResources.addAll(method.getRequestedResources());
        }
    }

    @Override
    public void tagFields() {
        List<FieldEntity> fields = getFields();
        for (FieldEntity field : fields) {
            field.extract();
            requestedResources.add(field.getJavaType());
        }
    }
}