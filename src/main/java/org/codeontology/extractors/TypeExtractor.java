package org.codeontology.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.TypeEntity;
import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Set;

public abstract class TypeExtractor<T extends CtType<?>> extends Extractor<T> {

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

    /*@Override
    public void extract() {
        if (getElement() instanceof CtEnum<?>) {
            new EnumExtractor<>((CtEnum<?>) getElement()).extract();
        } else if (getElement() instanceof CtClass<?>) {
            new ClassExtractor<>((CtClass<?>) getElement()).extract();
        } else if (getElement() instanceof CtInterface<?>) {
            new InterfaceExtractor((CtInterface<?>) getElement()).extract();
        } else if (getElement() instanceof CtAnnotationType<?>) {
            new AnnotationExtractor((CtAnnotationType<?>) getElement()).extract();
        } else if (getElement() == null) {
            switch (TypeEntity.getEntity(getReference())) {
                case CLASS: new ClassExtractor<>(getReference()).extract(); break;
                case INTERFACE: new InterfaceExtractor(getReference()).extract(); break;
                case ENUM: new EnumExtractor<>(getReference()).extract(); break;
                case ANNOTATION: new AnnotationExtractor(getReference()).extract(); break;
            }
        }
    }*/

    public CtTypeReference<?> getReference() {
        // todo: remove this method as it only makes sense to handle null references
        return (CtTypeReference<?>) super.getReference();
    }

    protected void tagSuperClass() {
        CtTypeReference<?> superclass = getReference().getSuperclass();
        if (superclass != null) {
            TypeExtractor<?> extractor = getFactory().getExtractor(superclass);
            addStatement(Ontology.getExtendsProperty(), extractor.getResource());
            if (superclass.getDeclaration() == null) {
                extractor.extract();
            }
        }
    }

    protected void tagSuperInterfaces() {
        Set<CtTypeReference<?>> references = getReference().getSuperInterfaces();

        for (CtTypeReference<?> reference : references) {
            TypeExtractor<?> extractor = getFactory().getExtractor(reference);
            addStatement(Ontology.getImplementsProperty(), extractor.getResource());
            if (reference.getDeclaration() == null) {
                extractor.extract();
            }
        }
    }

    protected void tagMethods() {
        Set<CtMethod<?>> methods = getElement().getMethods();

        for (CtMethod<?> method : methods) {
            getFactory().getExtractor(method).extract();
        }
    }

    protected void tagFields() {
        List<CtField<?>> fields = getElement().getFields();

        for (CtField<?> field : fields) {
            getFactory().getExtractor(field).extract();
        }
    }
}
