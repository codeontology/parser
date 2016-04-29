package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.reference.CtTypeReference;

public class AnnotationWrapper extends TypeWrapper<CtAnnotationType<?>> {

    public AnnotationWrapper(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ANNOTATION_ENTITY;
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
