package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Property;
import org.codeontology.CodeOntology;
import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TypeWrapper<T extends CtType<?>> extends Wrapper<T> {

    private Set<MethodWrapper> methods;

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
        if (isDeclarationAvailable() || CodeOntology.isJarExplorationEnabled()) {
            Set<MethodWrapper> methods = getMethods();
            for (MethodWrapper method : methods) {
                method.extract();
            }
        }
    }

    public Set<MethodWrapper> getMethods() {
        if (methods == null) {
            setMethods();
        }
        return methods;
    }

    private void setMethods() {
        methods = new HashSet<>();

        if (isDeclarationAvailable()) {
            Set<CtMethod<?>> ctMethods = getElement().getMethods();
            for (CtMethod ctMethod : ctMethods) {
                methods.add(getFactory().wrap(ctMethod));
            }
        } else {
            Method[] actualMethods = getReference().getActualClass().getDeclaredMethods();
            for (Method actualMethod : actualMethods) {
                CtExecutableReference<?> reference = ReflectionFactory.getInstance().createMethod(actualMethod);
                methods.add((MethodWrapper) getFactory().wrap(reference));
            }
        }
    }

    public void tagFields() {
        List<CtField<?>> fields = getElement().getFields();

        for (CtField<?> field : fields) {
            getFactory().wrap(field).extract();
        }
    }
}
