package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.*;

import java.lang.reflect.Executable;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class TypeVariableWrapper extends TypeWrapper<CtType<?>> {

    private int position;
    private List<CtTypeReference<?>> bounds;

    public TypeVariableWrapper(CtTypeReference<?> reference) {
        super(reference);
        setBounds();
    }

    private void setBounds() {
        bounds = new ArrayList<>();
        CtTypeParameterReference reference = (CtTypeParameterReference) getReference();
        CtTypeReference<?> boundingType = reference.getBoundingType();
        if (boundingType instanceof CtIntersectionTypeReference) {
            bounds = boundingType.asCtIntersectionTypeReference().getBounds();
        } else if (boundingType != null) {
            bounds.add(boundingType);
        }
    }

    @Override
    public void extract() {
        tagType();
        tagBounds();
        tagPosition();
    }

    private void tagPosition() {
        getLogger().addTriple(this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
    }

    public void tagBounds() {
        for (CtTypeReference bound : bounds) {
            tagBound(bound);
        }
    }

    private void tagBound(CtTypeReference boundReference) {
        TypeWrapper<?> bound = getFactory().wrap(boundReference);
        bound.setParent(this.getParent());
        if (((CtTypeParameterReference) getReference()).isUpper()) {
            getLogger().addTriple(this, Ontology.EXTENDS_PROPERTY, bound);
        } else {
            getLogger().addTriple(this, Ontology.SUPER_PROPERTY, bound);
        }
        bound.follow();
    }

    @Override
    public String buildRelativeURI() {
        if (isWildcard()) {
            return wildcardURI();
        }
        return getReference().getQualifiedName() + ":" + getParent().getRelativeURI();
    }

    private String wildcardURI() {
        String clause;
        if (((CtTypeParameterReference) getReference()).isUpper()) {
            clause = "extends";
        } else {
            clause = "super";
        }

        String uri = "?";
        String separator = "_";

        if (bounds.size() > 0) {
            uri = uri + separator + clause;
        }

        for (CtTypeReference bound : bounds) {
            TypeWrapper<?> wrapper = getFactory().wrap(bound);
            wrapper.setParent(getParent());
            uri = uri + separator + wrapper.getRelativeURI();
        }

        return uri;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.TYPE_VARIABLE_ENTITY;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private void setParent(CtTypeReference reference) {
        super.setParent(getFactory().wrap(reference));
    }

    private void setParent(CtType type) {
        setParent(type.getReference());
    }

    private void setParent(Class<?> clazz) {
        CtTypeReference<?> parent = getReference().getFactory().Class().createReference(clazz);
        setParent(parent);
    }

    private void findAndSetParent(ExecutableWrapper<?> executable) {
        CtExecutableReference<?> executableReference = (CtExecutableReference<?>) executable.getReference();

        if (executable.isDeclarationAvailable()) {
            List<CtTypeReference<?>> parameters = new TypeVariableList(executable.getElement().getFormalTypeParameters());
            if (parameters.contains(getReference())) {
                super.setParent(executable);
            } else {
                findAndSetParent(executableReference.getDeclaringType());
            }
        } else {
            findAndSetParent(executableReference);
        }
    }

    private void findAndSetParent(CtExecutableReference<?> executableReference) {
        if (executableReference.getDeclaration() != null) {
            findAndSetParent(getFactory().wrap(executableReference));
        } else if (isWildcard()) {
            super.setParent(getFactory().wrap(executableReference));
        } else {
            Executable executable = ReflectionFactory.getInstance().createActualExecutable(executableReference);
            TypeVariable<?>[] typeParameters = executable.getTypeParameters();
            Class<?> declaringClass = executable.getDeclaringClass();

            for (TypeVariable current : typeParameters) {
                if (current.getName().equals(getReference().getQualifiedName())) {
                    super.setParent(getFactory().wrap(executableReference));
                    return;
                }
            }

            findAndSetParent(declaringClass);
        }
    }


    private void findAndSetParent(Class<?> clazz) {
        TypeVariable<?>[] typeParameters = clazz.getTypeParameters();

        for (TypeVariable variable : typeParameters) {
            if (variable.getName().equals(getReference().getQualifiedName())) {
                setParent(clazz);
                return;
            }
        }

        findAndSetParent(clazz.getDeclaringClass());
    }

    private void findAndSetParent(CtTypeReference<?> reference) {
        if (reference.getDeclaration() != null) {
            findAndSetParent(reference.getDeclaration());
        } else {
            findAndSetParent(reference.getActualClass());
        }
    }

    private void findAndSetParent(CtType type) {
        if (type != null) {
            List<CtTypeReference<?>> formalTypes = new TypeVariableList(type.getFormalTypeParameters());
            while (!formalTypes.contains(getReference())) {
                type = type.getDeclaringType();
                formalTypes = new TypeVariableList(type.getFormalTypeParameters());
            }
            setParent(type);
        }
    }

    @Override
    public void setParent(Wrapper<?> wrapper) {
        CtReference reference = wrapper.getReference();
        if (reference instanceof CtTypeReference<?>) {
            findAndSetParent((CtTypeReference) reference);
        } else if (reference instanceof CtExecutableReference) {
            findAndSetParent((CtExecutableReference) reference);
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