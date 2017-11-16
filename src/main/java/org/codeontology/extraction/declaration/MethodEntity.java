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

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.docparser.DocCommentParser;
import org.codeontology.docparser.Tag;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.support.FormalTypeParametersTagger;
import org.codeontology.extraction.support.GenericDeclarationEntity;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

public class MethodEntity extends ExecutableEntity<CtMethod<?>> implements GenericDeclarationEntity<CtMethod<?>> {
    public MethodEntity(CtMethod<?> method) {
        super(method);
    }

    public MethodEntity(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.METHOD_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagReturns();
        if (isDeclarationAvailable()) {
            tagOverrides();
            tagFormalTypeParameters();
            tagReturnDescription();
        }
    }

    public void tagOverrides() {
        try {
            CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference()).getOverridingExecutable();
            if (reference != null) {
                ExecutableEntity overridingMethod = getFactory().wrap(reference);
                getLogger().addTriple(this, Ontology.OVERRIDES_PROPERTY, overridingMethod);
                overridingMethod.follow();
            }
        } catch (Exception | Error e) {
            // could not get an overriding executable
        }
    }

    public void tagReturns() {
        getLogger().addTriple(this, Ontology.RETURN_TYPE_PROPERTY, getReturnType());
    }

    private TypeEntity getReturnType() {
        TypeEntity<?> returnType = getGenericReturnType();
        if (returnType != null) {
            return returnType;
        }

        CtTypeReference<?> reference = ((CtExecutableReference<?>) getReference()).getType();
        returnType = getFactory().wrap(reference);
        returnType.setParent(this);
        returnType.follow();

        return returnType;
    }

    private TypeEntity getGenericReturnType() {
        if (!isDeclarationAvailable()) {
            return null;
        }
        try {
            CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference());
            Method method = (Method) ReflectionFactory.getInstance().createActualExecutable(reference);
            Type returnType = method.getGenericReturnType();

            if (returnType instanceof GenericArrayType ||
                returnType instanceof TypeVariable<?> ) {

                TypeEntity<?> result = getFactory().wrap(returnType);
                result.setParent(this);
                return result;
            }

            return null;

        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public List<TypeVariableEntity> getFormalTypeParameters() {
        return FormalTypeParametersTagger.formalTypeParametersOf(this);
    }

    @Override
    public void tagFormalTypeParameters() {
        new FormalTypeParametersTagger(this).tagFormalTypeParameters();
    }

    public String getReturnDescription() {
        String comment = getElement().getDocComment();
        if (comment == null) {
            return null;
        }

        DocCommentParser parser = new DocCommentParser(comment);
        List<Tag> tags = parser.getReturnTags();
        if (tags.isEmpty()) {
            return null;
        }

        return tags.get(0).getText();
    }

    public void tagReturnDescription() {
        String description = getReturnDescription();
        if (getReturnDescription() != null) {
            Literal literal = getModel().createTypedLiteral(description);
            getLogger().addTriple(this, Ontology.RETURN_DESCRIPTION_PROPERTY, literal);
        }
    }
}