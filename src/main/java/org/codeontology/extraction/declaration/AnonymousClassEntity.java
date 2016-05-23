package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnonymousClassEntity<T> extends ClassEntity<T> {

    public static final String TAG = "anonymous-class";
    private Set<Entity<?>> requestedResources;

    public AnonymousClassEntity(CtClass<T> anonymousClass) {
        super(anonymousClass);
        requestedResources = new HashSet<>();
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    public RDFNode getType() {
        return Ontology.ANONYMOUS_CLASS_ENTITY;
    }

    @Override
    public void extract() {
        tagType();
        tagSuperType();
        tagComment();
        tagFields();
        tagMethods();
        tagSourceCode();
        tagNestedTypes();
    }

    public void tagSuperType() {
        Set<CtTypeReference<?>> references = getReference().getSuperInterfaces();
        CtTypeReference<?> superTypeReference;
        if (references.isEmpty()) {
            superTypeReference = getReference().getSuperclass();
        } else {
            superTypeReference = (CtTypeReference<?>) references.toArray()[0];
        }
        TypeEntity<?> superType = getFactory().wrap(superTypeReference);
        superType.setParent(getParent());
        getLogger().addTriple(this, Ontology.IMPLEMENTS_PROPERTY, superType);
        requestedResources.add(superType);
        superType.follow();
    }

    public Set<Entity<?>> getRequestedResources() {
        return requestedResources;
    }

    @Override
    public void tagMethods() {
        List<MethodEntity> methods = getMethods();
        for (MethodEntity method : methods) {
            method.extract();
            requestedResources.addAll(method.getRequestedResources());
        }
    }

    @Override
    public void tagFields() {
        List<FieldEntity> fields = getFields();
        for (FieldEntity field : fields) {
            field.extract();
            requestedResources.add(field.getJavaType());
        }
    }
}
