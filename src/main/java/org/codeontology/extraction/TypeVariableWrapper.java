package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.apache.commons.lang3.ObjectUtils;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.reference.*;
import tdb.cmdline.CmdSub;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class TypeVariableWrapper extends TypeWrapper<CtType<?>> {

    private int position;
    private Wrapper parent;
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

    protected void tagBounds() {
        for (CtTypeReference bound : bounds) {
            tagBound(bound);
        }
    }

    private void tagBound(CtTypeReference boundReference) {
        TypeWrapper bound = getFactory().wrap(boundReference);
        if (bound instanceof TypeVariableWrapper) {
            ((TypeVariableWrapper) bound).findAndSetParent(parent);
        } else if (bound instanceof ArrayWrapper) {
            ((ArrayWrapper) bound).setParent(parent.getReference());
        } else if (bound instanceof ParameterizedTypeWrapper) {
            ((ParameterizedTypeWrapper) bound).setParent(parent.getReference());
        }
        if (((CtTypeParameterReference) getReference()).isUpper()) {
            getLogger().addTriple(this, Ontology.EXTENDS_PROPERTY, bound);
        } else {
            getLogger().addTriple(this, Ontology.SUPER_PROPERTY, bound);
        }
        if (!bound.isDeclarationAvailable()) {
            bound.extract();
        }
    }

    @Override
    public String getRelativeURI() {
        if (isWildcard()) {
            return wildcardURI();
        }
        return parent.getRelativeURI() + SEPARATOR + getReference().getQualifiedName();
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
            TypeWrapper wrapper = getFactory().wrap(bound);
            if (wrapper instanceof TypeVariableWrapper) {
                ((TypeVariableWrapper) wrapper).findAndSetParent(parent);
            }  else if (wrapper instanceof ArrayWrapper) {
                ((ArrayWrapper) wrapper).setParent(parent.getReference());
            } else if (wrapper instanceof ParameterizedTypeWrapper) {
                ((ParameterizedTypeWrapper) wrapper).setParent(parent.getReference());
                wrapper.extract();
            }
            uri = uri + separator + wrapper.getRelativeURI();
        }

        return uri;
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

    public void findAndSetParent(CtExecutableReference<?> executableReference) {
        if (executableReference.getDeclaration() != null) {
            findAndSetParent(getFactory().wrap(executableReference));
            return;
        }

        if (isWildcard()) {
            setParent(getFactory().wrap(executableReference));
            return;
        }

        Executable executable = getActualExecutable(executableReference);
        TypeVariable<?>[] typeParameters = executable.getTypeParameters();
        Class<?> declaringClass = executable.getDeclaringClass();

        for (TypeVariable current : typeParameters) {
            if (current.getName().equals(getReference().getQualifiedName())) {
                setParent(getFactory().wrap(executableReference));
                return;
            }
        }

        findAndSetParent(declaringClass);
    }


    private Executable getActualExecutable(CtExecutableReference<?> executableReference) {

        Executable executable = executableReference.getActualMethod();

        if (executable == null) {
            executable = executableReference.getActualConstructor();
        }

        if (executable == null) {
            try {
                Class<?> declaringClass = Class.forName(executableReference.getDeclaringType().getQualifiedName());

                Executable[] executables = declaringClass.getDeclaredMethods();
                if (executableReference.isConstructor()) {
                    executables = declaringClass.getDeclaredConstructors();
                }

                for (Executable current : executables) {
                    if (current.getName().equals(executableReference.getSimpleName()) || current instanceof Constructor) {
                        if (current.getParameterCount() == executableReference.getParameters().size()) {
                            List<CtTypeReference<?>> parameters = executableReference.getParameters();
                            Class<?>[] classes = new Class<?>[parameters.size()];
                            for (int i = 0; i < parameters.size(); i++) {
                                classes[i] = parameters.get(i).getActualClass();
                            }

                            boolean acc = true;

                            Class<?>[] parameterTypes = current.getParameterTypes();
                            for (int i = 0; i < classes.length && acc; i++) {
                                acc = classes[i].isAssignableFrom(parameterTypes[i]);
                            }

                            if (acc) {
                                executable = current;
                                break;
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException | NoSuchMethodError e) {
                throw new RuntimeException(e);
            }
        }

        return executable;
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

    public void findAndSetParent(Wrapper wrapper) {
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