package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Set;

public class ClassExtractor<T> extends TypeExtractor<CtClass<T>> {

    private ModifiableTagger modifiableTagger;

    public ClassExtractor(CtTypeReference<?> reference) {
        super(reference);
        modifiableTagger = new ModifiableTagger(this);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CLASS_CLASS;
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        if (isDeclarationAvailable()) {
            tagSuperClass();
            tagSuperInterfaces();
            tagComment();
            tagFields();
            tagConstructors();
            tagMethods();
            tagSourceCode();
            tagNestedTypes();
            tagVisibility();
            tagModifier();
        }
    }

    protected void tagSuperInterfaces() {
        tagSuperInterfaces(Ontology.IMPLEMENTS_PROPERTY);
    }

    protected void tagConstructors() {
        Set<CtConstructor<T>> constructors = getElement().getConstructors();

        for (CtConstructor<T> constructor : constructors) {
            getFactory().getExtractor(constructor).extract();
        }
    }

    protected void tagNestedTypes() {
        Set<CtType<?>> nestedTypes = getElement().getNestedTypes();
        for (CtType<?> type : nestedTypes) {
            Extractor extractor = getFactory().getExtractor(type);
            addTriple(extractor, Ontology.IS_NESTED_IN_PROPERTY, this.getResource());
            extractor.extract();
        }
    }

    protected void tagVisibility() {
        modifiableTagger.tagVisibility();
    }

    protected void tagModifier() {
        modifiableTagger.tagModifier();
    }
}
