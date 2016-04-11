package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.reference.CtTypeReference;

public class AnnotationExtractor extends TypeExtractor<CtAnnotationType<?>> {

    public AnnotationExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ANNOTATION_CLASS;
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
