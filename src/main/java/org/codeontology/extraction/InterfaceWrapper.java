package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public class InterfaceWrapper extends TypeWrapper<CtInterface<?>> implements GenericDeclarationWrapper<CtInterface<?>> {

    public InterfaceWrapper(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.INTERFACE_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagSuperInterfaces();
        tagModifiers();
        if (isDeclarationAvailable() || CodeOntology.isJarExplorationEnabled()) {
            tagFields();
            tagMethods();
        }
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagSourceCode();
            tagComment();
            tagFormalTypeParameters();
        }
    }

    public void tagSuperInterfaces() {
        tagSuperInterfaces(Ontology.EXTENDS_PROPERTY);
    }

    @Override
    public List<TypeVariableWrapper> getFormalTypeParameters() {
        return FormalTypeParametersTagger.formalTypeParametersOf(this);
    }

    @Override
    public void tagFormalTypeParameters() {
        new FormalTypeParametersTagger(this).tagFormalTypeParameters();
    }
}
