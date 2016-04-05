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
        tagSuperClass();
        tagSuperInterfaces();
        if (isDeclarationAvailable()) {
            tagSourceCode();
            tagComment();
            extractMembers();
        }
    }

    protected void extractMembers() {
        Set<CtMethod<?>> methods = getElement().getMethods();
        Set<CtConstructor<T>> constructors = getElement().getConstructors();
        List<CtField<?>> fields = getElement().getFields();

        for (CtMethod<?> method : methods) {
            getFactory().getExtractor(method).extract();
        }

        for (CtConstructor<T> constructor : constructors) {
            getFactory().getExtractor(constructor).extract();
        }

        for (CtField<?> field : fields) {
            getFactory().getExtractor(field).extract();
        }
    }

    protected void tagSuperClass() {
        CtTypeReference<?> superclass = getReference().getSuperclass();
        if (superclass != null) {
            TypeExtractor<?> extractor = getFactory().getExtractor(superclass);
            addStatement(Ontology.getExtendsProperty(), extractor.getResource());
            if (superclass.getDeclaration() == null) {
                extractor.extract();
            }
        }
    }

    protected void tagSuperInterfaces() {
        Set<CtTypeReference<?>> references = getReference().getSuperInterfaces();

        for (CtTypeReference<?> reference : references) {
            TypeExtractor<?> extractor = getFactory().getExtractor(reference);
            addStatement(Ontology.getImplementsProperty(), extractor.getResource());
            if (reference.getDeclaration() == null) {
                extractor.extract();
            }
        }
    }

}
