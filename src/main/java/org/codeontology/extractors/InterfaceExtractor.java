package org.codeontology.extractors;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.reference.CtTypeReference;

public class InterfaceExtractor extends TypeExtractor<CtInterface<?>> {

    private ModifiableTagger modifiableTagger;

    public InterfaceExtractor(CtTypeReference<?> reference) {
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
