package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.TypeEntity;
import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

public class TypeExtractor<T extends CtType<?>> extends Extractor<T> {

    public TypeExtractor(T type) {
        super(type);
        checkNullType();
    }

    public TypeExtractor(CtTypeReference<?> reference) {
        super(reference);
        checkNullType();
    }

    private void checkNullType() {
        if (getReference().getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            throw new NullTypeException();
        }
    }

    @Override
    protected String getRelativeURI() {
        return getReference().getQualifiedName();
    }

    @Override
    protected RDFNode getType() {
        return null;
    }

    @Override
    public void extract() {
        if (getElement() instanceof CtClass<?>) {
            new ClassExtractor<>((CtClass<?>) getElement()).extract();
        } else if (getElement() instanceof CtInterface<?>) {
            new InterfaceExtractor((CtInterface<?>) getElement()).extract();
        } else if (getElement() == null) {
            switch (TypeEntity.getEntity(getReference())) {
                case CLASS: new ClassExtractor<>(getReference()).extract(); break;
                case INTERFACE: new InterfaceExtractor(getReference()).extract(); break;
            }
        }
    }

    public CtTypeReference<?> getReference() {
        // todo: remove this method as it only makes sense to handle null references
        return (CtTypeReference<?>) super.getReference();
    }
}
