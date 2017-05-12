package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.reference.CtTypeReference;

public class AnnotationEntity extends TypeEntity<CtAnnotationType<?>> {

    public AnnotationEntity(CtTypeReference<?> reference) {
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
        tagLabel();
        if (isDeclarationAvailable()) {
            tagComment();
            tagSourceCode();
        }
    }
}
