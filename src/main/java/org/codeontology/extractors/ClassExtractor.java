package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Set;


public class ClassExtractor<T> extends TypeExtractor<CtClass<T>> {

    public ClassExtractor(CtClass<T> clazz) {
        super(clazz);
    }

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
        }
    }

    protected void tagConstructors() {
        Set<CtConstructor<T>> constructors = getElement().getConstructors();

        for (CtConstructor<T> constructor : constructors) {
            getFactory().getExtractor(constructor).extract();
        }
    }

}
