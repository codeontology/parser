package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.extraction.support.FormalTypeParametersTagger;
import org.codeontology.extraction.support.GenericDeclarationEntity;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public class InterfaceEntity extends TypeEntity<CtInterface<?>> implements GenericDeclarationEntity<CtInterface<?>> {

    public InterfaceEntity(CtTypeReference<?> reference) {
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
        tagLabel();
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
    public List<TypeVariableEntity> getFormalTypeParameters() {
        return FormalTypeParametersTagger.formalTypeParametersOf(this);
    }

    @Override
    public void tagFormalTypeParameters() {
        new FormalTypeParametersTagger(this).tagFormalTypeParameters();
    }
}
