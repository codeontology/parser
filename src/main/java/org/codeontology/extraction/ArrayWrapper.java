package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

public class ArrayWrapper extends TypeWrapper<CtType<?>> {
    private TypeWrapper<?> componentType;

    public ArrayWrapper(CtTypeReference<?> reference) {
        super(reference);
        CtTypeReference<?> componentReference = ((CtArrayTypeReference<?>) getReference()).getComponentType();
        componentType = getFactory().wrap(componentReference);
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
        return Ontology.ARRAY_ENTITY;
    }

    @Override
    public String buildRelativeURI() {
        return componentType.getRelativeURI() + "[]";
    }

    public void tagArrayOf() {
        getLogger().addTriple(this, Ontology.ARRAY_OF_PROPERTY, componentType);
        componentType.follow();
    }

    public void tagDimensions() {
        int dimensions = ((CtArrayTypeReference<?>) getReference()).getDimensionCount();
        getLogger().addTriple(this, Ontology.DIMENSIONS_PROPERTY, getModel().createTypedLiteral(dimensions));
    }

    @Override
    public void setParent(Wrapper<?> parent) {
        super.setParent(parent);
        componentType.setParent(parent);
    }
}
