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

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.support.*;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public class LocalVariableEntity extends NamedElementEntity<CtLocalVariable<?>>
        implements MemberEntity<CtLocalVariable<?>>, TypedElementEntity<CtLocalVariable<?>>, ModifiableEntity<CtLocalVariable<?>> {

    public LocalVariableEntity(CtLocalVariable<?> variable) {
        super(variable);
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagLabel();
        tagJavaType();
        tagModifiers();
        tagDeclaringElement();
        tagSourceCode();
    }

    @Override
    public List<Modifier> getModifiers() {
        return Modifier.asList(getElement().getModifiers());
    }

    @Override
    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.LOCAL_VARIABLE_ENTITY;
    }

    @Override
    public Entity<?> getDeclaringElement() {
        return getParent();
    }

    public void tagDeclaringElement() {
        new DeclaringElementTagger(this).tagDeclaredBy();
    }

    @Override
    public TypeEntity<?> getJavaType() {
        CtTypeReference<?> type = getElement().getType();
        TypeEntity<?> entity = getFactory().wrap(type);
        entity.setParent(getParent(ExecutableEntity.class, TypeEntity.class));
        return entity;
    }

    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType();
    }
}