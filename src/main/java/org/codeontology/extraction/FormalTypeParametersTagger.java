package org.codeontology.extraction;

import org.codeontology.Ontology;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class FormalTypeParametersTagger {
    private GenericDeclarationWrapper<?> genericDeclaration;

    public FormalTypeParametersTagger(GenericDeclarationWrapper<?> genericDeclaration) {
        this.genericDeclaration = genericDeclaration;
    }

    public void tagFormalTypeParameters() {
        List<TypeVariableWrapper> parameters = genericDeclaration.getFormalTypeParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            TypeVariableWrapper typeVariable = parameters.get(i);
            typeVariable.setParent(genericDeclaration);
            typeVariable.setPosition(i);
            RDFLogger.getInstance().addTriple(genericDeclaration, Ontology.FORMAL_TYPE_PARAMETER_PROPERTY, typeVariable);
            typeVariable.extract();
        }
    }

    public static List<TypeVariableWrapper> formalTypeParametersOf(GenericDeclarationWrapper<?> genericDeclaration) {
        List<CtTypeReference<?>> parameters = genericDeclaration.getElement().getFormalTypeParameters();
        List<TypeVariableWrapper> typeVariables = new ArrayList<>();

        for (CtTypeReference parameter : parameters) {
            Wrapper<?> wrapper = WrapperFactory.getInstance().wrap(parameter);
            if (wrapper instanceof TypeVariableWrapper) {
                typeVariables.add((TypeVariableWrapper) wrapper);
            }
        }

        return typeVariables;
    }
}
