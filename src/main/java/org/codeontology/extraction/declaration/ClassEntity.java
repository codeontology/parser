package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.support.FormalTypeParametersTagger;
import org.codeontology.extraction.support.GenericDeclarationEntity;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassEntity<T> extends TypeEntity<CtClass<T>> implements GenericDeclarationEntity<CtClass<T>> {

    private List<ConstructorEntity> constructors;

    public ClassEntity(CtClass<T> clazz) {
        super(clazz);
    }

    public ClassEntity(CtTypeReference<?> reference) {
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
        TypeEntity<?> superClass = getFactory().wrap(superclass);
        superClass.setParent(this);
        getLogger().addTriple(this, Ontology.EXTENDS_PROPERTY, superClass);
        superClass.follow();
    }

    public void tagSuperInterfaces() {
        tagSuperInterfaces(Ontology.IMPLEMENTS_PROPERTY);
    }

    public void tagConstructors() {
        getConstructors().forEach(ConstructorEntity::extract);
    }

    public List<ConstructorEntity> getConstructors() {
        if (constructors == null) {
            setConstructors();
        }

        return constructors;
    }

    private void setConstructors() {
        constructors = new ArrayList<>();

        if (!isDeclarationAvailable()) {
            setConstructorsByReflection();
            return;
        }
        Set<CtConstructor<T>> ctConstructors = getElement().getConstructors();
        for (CtConstructor ctConstructor : ctConstructors) {
            ConstructorEntity constructor = getFactory().wrap(ctConstructor);
            constructor.setParent(this);
            constructors.add(constructor);
        }
    }

    private void setConstructorsByReflection() {
        try {
            Constructor[] actualConstructors = getReference().getActualClass().getDeclaredConstructors();
            for (Constructor actualConstructor : actualConstructors) {
                CtExecutableReference<?> reference = ReflectionFactory.getInstance().createConstructor(actualConstructor);
                ConstructorEntity constructor = (ConstructorEntity) getFactory().wrap(reference);
                constructor.setParent(this);
                constructors.add(constructor);
            }
        } catch (Throwable t) {
            showMemberAccessWarning();
        }
    }

    public void tagNestedTypes() {
        Set<CtType<?>> nestedTypes = getElement().getNestedTypes();
        for (CtType<?> type : nestedTypes) {
            Entity entity = getFactory().wrap(type);
            getLogger().addTriple(entity, Ontology.DECLARED_BY_PROPERTY, this);
            entity.extract();
        }
    }

    @Override
    public List<TypeVariableEntity> getFormalTypeParameters() {
        return FormalTypeParametersTagger.formalTypeParametersOf(this);
    }

    public void tagFormalTypeParameters() {
        new FormalTypeParametersTagger(this).tagFormalTypeParameters();
    }
}
