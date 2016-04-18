package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

public class ArrayWrapper extends TypeWrapper<CtType<?>> {

    private CtReference parent;
    private TypeWrapper componentType;

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
        RDFWriter.addTriple(this, Ontology.ARRAY_OF_PROPERTY, componentType);
        if (!componentType.isDeclarationAvailable()) {
            componentType.extract();
        }
    }

    protected void tagDimensions() {
        int dimensions = ((CtArrayTypeReference<?>) getReference()).getDimensionCount();
        RDFWriter.addTriple(this, Ontology.DIMENSIONS_PROPERTY, getModel().createTypedLiteral(dimensions));
    }

    public void setParent(CtReference parent) {
        this.parent = parent;
        handleGenericArray();
    }

    private void handleGenericArray() {
        if (componentType instanceof TypeVariableWrapper) {
            TypeVariableWrapper typeVariable = (TypeVariableWrapper) componentType;

            if (parent instanceof CtTypeReference) {
                typeVariable.findAndSetParent((CtTypeReference) parent);
            } else if (parent instanceof CtExecutableReference) {
                typeVariable.findAndSetParent((CtExecutableReference) parent);
            }
        }
    }
}
