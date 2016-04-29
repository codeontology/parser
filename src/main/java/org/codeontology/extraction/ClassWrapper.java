package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassWrapper<T> extends TypeWrapper<CtClass<T>> implements ModifiableWrapper {

    private List<ConstructorWrapper> constructors;

    public ClassWrapper(CtClass<T> clazz) {
        super(clazz);
    }

    public ClassWrapper(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CLASS_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagSuperClass();
        tagSuperInterfaces();
        tagModifiers();
        if (isDeclarationAvailable() || CodeOntology.isJarExplorationEnabled()) {
            tagFields();
            tagConstructors();
            tagMethods();
        }
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagComment();
            tagSourceCode();
            tagNestedTypes();
            tagFormalTypeParameters();
        }
    }

    public void tagSuperClass() {
        CtTypeReference<?> superclass = getReference().getSuperclass();
        if (superclass == null) {
            superclass = ReflectionFactory.getInstance().createTypeReference(Object.class);
        }
        TypeWrapper<?> superClass = getFactory().wrap(superclass);
        superClass.setParent(this);
        getLogger().addTriple(this, Ontology.EXTENDS_PROPERTY, superClass);
        superClass.follow();
    }

    public void tagSuperInterfaces() {
        tagSuperInterfaces(Ontology.IMPLEMENTS_PROPERTY);
    }

    public void tagConstructors() {
        List<ConstructorWrapper> constructors = getConstructors();

        for (ConstructorWrapper constructor : constructors) {
            constructor.extract();
        }
    }

    public List<ConstructorWrapper> getConstructors() {
        if (constructors == null) {
            setConstructors();
        }

        return constructors;
    }

    private void setConstructors() {
        constructors = new ArrayList<>();

        if (isDeclarationAvailable()) {
            Set<CtConstructor<T>> ctConstructors = getElement().getConstructors();
            for (CtConstructor ctConstructor : ctConstructors) {
                constructors.add(getFactory().wrap(ctConstructor));
            }
        } else {
            Constructor[] actualConstructors = getReference().getActualClass().getDeclaredConstructors();
            for (Constructor actualConstructor : actualConstructors) {
                CtExecutableReference<?> reference = ReflectionFactory.getInstance().createConstructor(actualConstructor);
                constructors.add((ConstructorWrapper) getFactory().wrap(reference));
            }
        }
    }

    public void tagNestedTypes() {
        Set<CtType<?>> nestedTypes = getElement().getNestedTypes();
        for (CtType<?> type : nestedTypes) {
            Wrapper wrapper = getFactory().wrap(type);
            getLogger().addTriple(wrapper, Ontology.IS_NESTED_IN_PROPERTY, this);
            wrapper.extract();
        }
    }

    public void tagFormalTypeParameters() {
        new FormalTypeParametersTagger(this).tagFormalTypeParameters();
    }
}
