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
        return Ontology.ARRAY_CLASS;
    }

    @Override
    public String getRelativeURI() {
        return componentType.getRelativeURI() + "[]";
    }

    protected void tagArrayOf() {
        getLogger().addTriple(this, Ontology.ARRAY_OF_PROPERTY, componentType);
        if (!componentType.isDeclarationAvailable()) {
            componentType.extract();
        }
    }

    protected void tagDimensions() {
        int dimensions = ((CtArrayTypeReference<?>) getReference()).getDimensionCount();
        getLogger().addTriple(this, Ontology.DIMENSIONS_PROPERTY, getModel().createTypedLiteral(dimensions));
    }

    @Override
    public void setParent(Wrapper<?> parent) {
        super.setParent(parent);
        componentType.setParent(parent);
    }
}
