package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Property;
import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Set;

public abstract class TypeWrapper<T extends CtType<?>> extends Wrapper<T> {

    public TypeWrapper(T type) {
        super(type);
        checkNullType();
    }

    public TypeWrapper(CtTypeReference<?> reference) {
        super(reference);
        checkNullType();
    }

    private void checkNullType() {
        if (getReference().getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            throw new NullTypeException();
        }
    }

    @Override
    public String buildRelativeURI() {
        return getReference().getQualifiedName();
    }

    public CtTypeReference<?> getReference() {
        return (CtTypeReference<?>) super.getReference();
    }

    public void tagSuperInterfaces(Property property) {
        Set<CtTypeReference<?>> references = getReference().getSuperInterfaces();

        for (CtTypeReference<?> reference : references) {
            TypeWrapper<?> superInterface = getFactory().wrap(reference);
            superInterface.setParent(this);
            getLogger().addTriple(this, property, superInterface.getResource());
            superInterface.follow();
        }
    }

    public void tagMethods() {
        Set<CtMethod<?>> methods = getElement().getMethods();

        for (CtMethod<?> method : methods) {
            getFactory().wrap(method).extract();
        }
    }

    public void tagFields() {
        List<CtField<?>> fields = getElement().getFields();

        for (CtField<?> field : fields) {
            getFactory().wrap(field).extract();
        }
    }
}
