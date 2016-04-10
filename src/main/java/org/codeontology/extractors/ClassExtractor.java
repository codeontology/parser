package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Set;

public class ClassExtractor<T> extends TypeExtractor<CtClass<T>> {

    public ClassExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.getClassIndividual();
    }

    @Override
    public void extract() {
        tagClass();
        writeRDF();
    }

    protected void tagClass() {
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
        }
    }

    protected void tagSuperInterfaces() {
        tagSuperInterfaces(Ontology.getImplementsProperty());
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
            addStatement(Ontology.getContainsProperty(), extractor.getResource());
            extractor.extract();
        }
    }
}
