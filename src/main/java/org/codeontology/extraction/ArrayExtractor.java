package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

public class ArrayExtractor extends TypeExtractor<CtType<?>> {
    public ArrayExtractor(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagArrayOf();
        tagDimensions();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ARRAY_CLASS;
    }

    @Override
    public String getRelativeURI() {
        return getReference().getQualifiedName();
    }


    protected void tagArrayOf() {
        CtTypeReference<?> componentType = ((CtArrayTypeReference<?>) getReference()).getComponentType();
        Extractor componentTypeExtractor = getFactory().getExtractor(componentType);
        addTriple(this, Ontology.ARRAY_OF_PROPERTY, componentTypeExtractor.getResource());
        if (!componentTypeExtractor.isDeclarationAvailable()) {
            componentTypeExtractor.extract();
        }
    }

    protected void tagDimensions() {
        int dimensions = ((CtArrayTypeReference<?>) getReference()).getDimensionCount();
        addTriple(this, Ontology.ARRAY_OF_PROPERTY, getModel().createTypedLiteral(dimensions));
    }
}
