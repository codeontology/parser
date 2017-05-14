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
import org.codeontology.extraction.EntityRegister;
import org.codeontology.extraction.ReflectionFactory;
import spoon.reflect.declaration.CtType;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class ParameterizedTypeEntity extends TypeEntity<CtType<?>> {
    private List<CtTypeReference<?>> arguments;
    private boolean diamond = false;

    public ParameterizedTypeEntity(CtTypeReference<?> reference) {
        super(reference);
        arguments = getReference().getActualTypeArguments();
        if (!arguments.isEmpty()) {
            if (arguments.get(0) instanceof CtImplicitTypeReference<?>) {
                diamond = true;
            }
        }
    }

    @Override
    public String buildRelativeURI() {
        String uri = getReference().getQualifiedName();
        String argumentsString = "";
        Entity<?> parent = getParent();

        if (diamond) {
            return uri + SEPARATOR + "diamond";
        }

        for (CtTypeReference<?> argument : arguments) {
            TypeEntity<?> argumentEntity = getFactory().wrap(argument);

            argumentEntity.setParent(parent);

            if (argumentsString.equals("")) {
                argumentsString = argumentEntity.getRelativeURI();
            } else {
                argumentsString = argumentsString + SEPARATOR + argumentEntity.getRelativeURI();
            }
        }

        uri = uri + "[" + argumentsString + "]";
        uri = uri.replace(" ", "_");

        return uri;
    }

    @Override
    public void extract() {
        tagType();
        tagGenericType();
        tagActualTypeArguments();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PARAMETERIZED_TYPE_ENTITY;
    }

    public void tagGenericType() {
        TypeEntity genericType = getGenericType();
        genericType.follow();
        getLogger().addTriple(this, Ontology.GENERIC_TYPE_PROPERTY, genericType);
    }

    public TypeEntity<?> getGenericType() {
        CtTypeReference<?> cloneReference = ReflectionFactory.getInstance().clone(getReference());
        cloneReference.setActualTypeArguments(new ArrayList<>());
        return getFactory().wrap(cloneReference);
    }

    public void tagActualTypeArguments() {
        if (diamond) {
            return;
        }

        for (int i = 0; i < arguments.size(); i++) {
            TypeArgumentEntity typeArgument = new TypeArgumentEntity(arguments.get(i));
            typeArgument.setPosition(i);
            getLogger().addTriple(this, Ontology.ACTUAL_TYPE_ARGUMENT_PROPERTY, typeArgument);
            typeArgument.extract();
        }
    }

    @Override
    public void follow() {
        if (EntityRegister.getInstance().add(this))  {
            extract();
        }
    }

    class TypeArgumentEntity extends TypeEntity<CtType<?>> {

        private int position = 0;
        private TypeEntity<?> argument;

        public TypeArgumentEntity(CtTypeReference reference) {
            super(reference);
            argument = getFactory().wrap(getReference());
            setParent(ParameterizedTypeEntity.this.getParent());
        }

        @Override
        public void extract() {
            tagType();
            tagJavaType();
            tagPosition();
        }

        private void tagJavaType() {
            getLogger().addTriple(this, Ontology.JAVA_TYPE_PROPERTY, argument);
            argument.follow();
        }

        private void tagPosition() {
            getLogger().addTriple(TypeArgumentEntity.this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
        }

        @Override
        public String buildRelativeURI() {
            return ParameterizedTypeEntity.this.getRelativeURI() + SEPARATOR + position;
        }

        @Override
        protected RDFNode getType() {
            return Ontology.TYPE_ARGUMENT_ENTITY;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void setParent(Entity<?> parent) {
            super.setParent(parent);
            argument.setParent(parent);
        }
    }
}