package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Set;

public class ClassWrapper<T> extends TypeWrapper<CtClass<T>> {

    private ModifiableTagger modifiableTagger;

    public ClassWrapper(CtTypeReference<?> reference) {
        super(reference);
        modifiableTagger = new ModifiableTagger(this);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CLASS_CLASS;
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagSuperClass();
            tagSuperInterfaces();
            tagComment();
            tagFields();
            tagConstructors();
            tagMethods();
            tagSourceCode();
            tagNestedTypes();
            tagVisibility();
            tagModifier();
            tagFormalTypeParameters();
        }
    }

    protected void tagSuperClass() {
        CtTypeReference<?> superclass = getReference().getSuperclass();
        if (superclass == null) {
            superclass = getFactory().getParent().Type().createReference(Object.class);
        }
        TypeWrapper<?> wrapper = getFactory().wrap(superclass);
        if (wrapper instanceof ParameterizedTypeWrapper) {
            ((ParameterizedTypeWrapper) wrapper).setParent(getReference());
        }
        getLogger().addTriple(this, Ontology.EXTENDS_PROPERTY, wrapper);
        if (!wrapper.isDeclarationAvailable()) {
            wrapper.extract();
        }
    }

    protected void tagSuperInterfaces() {
        tagSuperInterfaces(Ontology.IMPLEMENTS_PROPERTY);
    }

    protected void tagConstructors() {
        Set<CtConstructor<T>> constructors = getElement().getConstructors();

        for (CtConstructor<T> constructor : constructors) {
            getFactory().wrap(constructor).extract();
        }
    }

    protected void tagNestedTypes() {
        Set<CtType<?>> nestedTypes = getElement().getNestedTypes();
        for (CtType<?> type : nestedTypes) {
            Wrapper wrapper = getFactory().wrap(type);
            getLogger().addTriple(wrapper, Ontology.IS_NESTED_IN_PROPERTY, this.getResource());
            wrapper.extract();
        }
    }

    protected void tagVisibility() {
        modifiableTagger.tagVisibility();
    }

    protected void tagModifier() {
        modifiableTagger.tagModifier();
    }

    protected void tagFormalTypeParameters() {
        List<CtTypeReference<?>> parameters = getElement().getFormalTypeParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            TypeVariableWrapper wrapper = ((TypeVariableWrapper) getFactory().wrap(parameters.get(i)));
            wrapper.setParent(this);
            wrapper.setPosition(i);
            getLogger().addTriple(this, Ontology.FORMAL_TYPE_PARAMETER_PROPERTY, wrapper);
            wrapper.extract();
        }
    }
}
