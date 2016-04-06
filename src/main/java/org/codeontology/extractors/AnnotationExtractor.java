package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.reference.CtTypeReference;

public class AnnotationExtractor extends TypeExtractor<CtAnnotationType<?>> {

    public AnnotationExtractor(CtAnnotationType<?> type) {
        super(type);
    }

    public AnnotationExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.getAnnotationIndividual();
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        if (isDeclarationAvailable()) {
            tagComment();
            tagSourceCode();
        }
    }
}
