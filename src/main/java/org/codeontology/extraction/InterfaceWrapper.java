package org.codeontology.extraction;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.reference.CtTypeReference;

public class InterfaceWrapper extends TypeWrapper<CtInterface<?>> {

    private ModifiableTagger modifiableTagger;

    public InterfaceWrapper(CtTypeReference<?> reference) {
        super(reference);
        modifiableTagger = new ModifiableTagger(this);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.INTERFACE_CLASS;
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagSuperInterfaces();
            tagFields();
            tagMethods();
            tagSourceCode();
            tagComment();
            tagVisibility();
            tagModifiers();
        }
    }

    protected void tagSuperInterfaces() {
        tagSuperInterfaces(Ontology.EXTENDS_PROPERTY);
    }

    protected void tagVisibility() {
        modifiableTagger.tagVisibility();
    }

    protected void tagModifiers() {
        modifiableTagger.tagModifier();
    }
}
