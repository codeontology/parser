package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class TypeVariableWrapper extends TypeWrapper<CtType<?>> {

    private int position;
    private Wrapper parent;

    public TypeVariableWrapper(CtTypeReference<?> reference) {
        super(reference);
    }

    @Override
    public void extract() {
        tagType();
        tagBounds();
        tagPosition();
    }

    private void tagPosition() {
        RDFWriter.addTriple(this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
    }

    protected void tagBounds() {
        CtTypeParameterReference reference = (CtTypeParameterReference) getReference();
        CtTypeReference<?> boundingType = reference.getBoundingType();
        if (boundingType instanceof CtIntersectionTypeReference) {
            List<CtTypeReference<?>> bounds = boundingType.asCtIntersectionTypeReference().getBounds();
            for (CtTypeReference bound : bounds) {
                tagBound(bound);
            }
        } else if (boundingType != null) {
            tagBound(boundingType);
        }
    }

    private void tagBound(CtTypeReference boundReference) {
        TypeWrapper bound = getFactory().wrap(boundReference);
        if (bound instanceof TypeVariableWrapper) {
            //((TypeVariableWrapper) bound).findAndSetParent(parent);
            return;
        }
        RDFWriter.addTriple(this, Ontology.EXTENDS_PROPERTY, bound);
        if (!bound.isDeclarationAvailable()) {
            bound.extract();
        }
    }

    @Override
    public String getRelativeURI() {
        if (isWildcard()) {
            return "?";
        }

        return parent.getRelativeURI() + SEPARATOR + getReference().getQualifiedName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.TYPE_VARIABLE_CLASS;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setParent(Wrapper parent) {
        this.parent = parent;
    }

    public void setParent(CtTypeReference reference) {
        setParent(getFactory().wrap(reference));
    }

    public void setParent(CtType type) {
        setParent(type.getReference());
    }

    private void setParent(Class<?> clazz) {
        CtTypeReference<?> parent = getReference().getFactory().Class().createReference(clazz);
        setParent(parent);
    }

    public void findAndSetParent(ExecutableWrapper<?> executable) {
        CtExecutableReference<?> executableReference = (CtExecutableReference<?>) executable.getReference();

        if (executable.isDeclarationAvailable()) {
            List<CtTypeReference<?>> parameters = new TypeVariableList(executable.getElement().getFormalTypeParameters());
            if (parameters.contains(getReference())) {
                setParent(executable);
            } else {
                findAndSetParent(executableReference.getDeclaringType());
            }
        } else {
            findAndSetParent(executableReference);
        }
    }

    public void findAndSetParent(CtExecutableReference executableReference) {
        if (executableReference.getDeclaration() != null) {
            findAndSetParent(getFactory().wrap(executableReference));
            return;
        }

        if (isWildcard()) {
            setParent(getFactory().wrap(executableReference));
            return;
        }

        Method method = executableReference.getActualMethod();
        Constructor<?> constructor = executableReference.getActualConstructor();

        TypeVariable<?>[] typeParameters;
        Class<?> declaringClass;

        try {
            typeParameters = method.getTypeParameters();
            declaringClass = method.getDeclaringClass();
        } catch (NullPointerException e) {
            typeParameters = constructor.getTypeParameters();
            declaringClass = constructor.getDeclaringClass();
        }

        for (TypeVariable current : typeParameters) {
            if (current.getName().equals(getReference().getQualifiedName())) {
                setParent(getFactory().wrap(executableReference));
                return;
            }
        }

        findAndSetParent(declaringClass);
    }

    public void findAndSetParent(Class<?> clazz) {
        TypeVariable<?>[] typeParameters = clazz.getTypeParameters();

        for (TypeVariable variable : typeParameters) {
            if (variable.getName().equals(getReference().getQualifiedName())) {
                setParent(clazz);
                return;
            }
        }

        findAndSetParent(clazz.getDeclaringClass());
    }

    public void findAndSetParent(CtTypeReference<?> reference) {
        if (reference.getDeclaration() != null) {
            findAndSetParent(reference.getDeclaration());
        } else {
            findAndSetParent(reference.getActualClass());
        }
    }

    public void findAndSetParent(CtType type) {
        if (type != null) {
            List<CtTypeReference<?>> formalTypes = new TypeVariableList(type.getFormalTypeParameters());
            while (!formalTypes.contains(getReference())) {
                type = type.getDeclaringType();
                formalTypes = new TypeVariableList(type.getFormalTypeParameters());
            }
            setParent(type);
        }
    }

    public boolean isWildcard() {
        return getReference().getSimpleName().equals("?");
    }
}

class TypeVariableList extends ArrayList<CtTypeReference<?>> {

    public TypeVariableList(List<CtTypeReference<?>> parameters) {
        super(parameters);
    }

    @Override
    public boolean contains(Object object) {
        if (!(object instanceof CtTypeParameterReference)) {
            return super.contains(object);
        }
        CtTypeReference<?> parameter = (CtTypeReference<?>) object;

        if (parameter.getSimpleName().equals("?")) {
            return true;
        }

        for (CtTypeReference currentParameter : this) {
            if (currentParameter.getQualifiedName().equals(parameter.getQualifiedName())) {
                return true;
            }
        }
        return false;
    }
}