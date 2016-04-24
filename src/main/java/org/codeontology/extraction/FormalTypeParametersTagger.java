package org.codeontology.extraction;

import org.codeontology.Ontology;
import spoon.reflect.declaration.CtGenericElement;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public class FormalTypeParametersTagger {
    private Wrapper<? extends CtGenericElement> genericDeclaration;

    public FormalTypeParametersTagger(Wrapper<? extends CtGenericElement> genericDeclaration) {
        this.genericDeclaration = genericDeclaration;
    }

    public void tagFormalTypeParameters() {
        List<CtTypeReference<?>> parameters = genericDeclaration.getElement().getFormalTypeParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            CtTypeReference<?> current = parameters.get(i);
            TypeVariableWrapper typeVariable = (TypeVariableWrapper) WrapperFactory.getInstance().wrap(current);
            typeVariable.setParent(genericDeclaration);
            typeVariable.setPosition(i);
            RDFLogger.getInstance().addTriple(genericDeclaration, Ontology.FORMAL_TYPE_PARAMETER_PROPERTY, typeVariable);
            typeVariable.extract();
        }
    }
}
