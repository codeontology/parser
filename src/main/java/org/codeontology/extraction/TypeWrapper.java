package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Property;
import org.codeontology.Ontology;
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
    protected String getRelativeURI() {
        return getReference().getQualifiedName();
    }

    public CtTypeReference<?> getReference() {
        return (CtTypeReference<?>) super.getReference();
    }

    protected void tagSuperClass() {
        CtTypeReference<?> superclass = getReference().getSuperclass();
        if (superclass != null) {
            TypeWrapper<?> wrapper = getFactory().wrap(superclass);
            getLogger().addTriple(this, Ontology.EXTENDS_PROPERTY, wrapper.getResource());
            if (superclass.getDeclaration() == null) {
                wrapper.extract();
            }
        }
    }

    protected void tagSuperInterfaces(Property property) {
        Set<CtTypeReference<?>> references = getReference().getSuperInterfaces();

        for (CtTypeReference<?> reference : references) {
            TypeWrapper<?> wrapper = getFactory().wrap(reference);

            getLogger().addTriple(this, property, wrapper.getResource());

            if (reference.getDeclaration() == null) {
                wrapper.extract();
            }
        }
    }

    protected void tagMethods() {
        Set<CtMethod<?>> methods = getElement().getMethods();

        for (CtMethod<?> method : methods) {
            getFactory().wrap(method).extract();
        }
    }

    protected void tagFields() {
        List<CtField<?>> fields = getElement().getFields();

        for (CtField<?> field : fields) {
            getFactory().wrap(field).extract();
        }
    }
}
