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
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

public class ArrayEntity extends TypeEntity<CtType<?>> {
    private TypeEntity<?> componentType;

    public ArrayEntity(CtTypeReference<?> reference) {
        super(reference);
        CtTypeReference<?> componentReference = ((CtArrayTypeReference<?>) getReference()).getArrayType();
        componentType = getFactory().wrap(componentReference);
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagLabel();
        tagArrayOf();
        tagDimensions();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ARRAY_ENTITY;
    }

    @Override
    public String buildRelativeURI() {
        return componentType.getRelativeURI() + "[]";
    }

    public void tagArrayOf() {
        getLogger().addTriple(this, Ontology.ARRAY_OF_PROPERTY, componentType);
        componentType.follow();
    }

    public void tagDimensions() {
        int dimensions = ((CtArrayTypeReference<?>) getReference()).getDimensionCount();
        getLogger().addTriple(this, Ontology.DIMENSIONS_PROPERTY, getModel().createTypedLiteral(dimensions));
    }

    @Override
    public void setParent(Entity<?> parent) {
        super.setParent(parent);
        componentType.setParent(parent);
    }

    @Override
    public String getName() {
        String componenTypeName = componentType.buildRelativeURI();
        if (componentType instanceof PrimitiveTypeEntity) {
            componenTypeName = componenTypeName.toLowerCase();
        }
        return componenTypeName + "[]";
    }
}