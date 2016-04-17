package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

public class ArrayWrapper extends TypeWrapper<CtType<?>> {
    public ArrayWrapper(CtTypeReference<?> reference) {
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
        return getReference().toString();
    }

    protected void tagArrayOf() {
        /*CtTypeReference<?> componentType = ((CtArrayTypeReference<?>) getReference()).getComponentType();
        Wrapper componentTypeWrapper = getFactory().wrap(componentType);
        RDFWriter.addTriple(this, Ontology.ARRAY_OF_PROPERTY, componentTypeWrapper.getResource());
        if (!componentTypeWrapper.isDeclarationAvailable()) {
            componentTypeWrapper.extract();
        }*/
    }

    protected void tagDimensions() {
        int dimensions = ((CtArrayTypeReference<?>) getReference()).getDimensionCount();
        RDFWriter.addTriple(this, Ontology.DIMENSIONS_PROPERTY, getModel().createTypedLiteral(dimensions));
    }
}
