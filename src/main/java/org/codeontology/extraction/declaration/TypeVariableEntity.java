/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

package org.codeontology.extraction.declaration;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.support.GenericDeclarationEntity;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.*;

import java.lang.reflect.Executable;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class TypeVariableEntity extends TypeEntity<CtType<?>> {

    private int position;
    private List<CtTypeReference<?>> bounds;

    public TypeVariableEntity(CtTypeReference<?> reference) {
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
        if (!CodeOntology.processGenerics()) {
            return;
        }
        tagType();
        tagBounds();
        tagPosition();
    }

    private void tagPosition() {
        getLogger().addTriple(this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
    }

    public void tagBounds() {
        bounds.forEach(this::tagBound);
    }

    private void tagBound(CtTypeReference boundReference) {
        TypeEntity<?> bound = getFactory().wrap(boundReference);
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
        if (!CodeOntology.processGenerics()) {
            return getReference().getSimpleName();
        }

        if (isWildcard() || getParent() == null) {
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
            TypeEntity<?> entity = getFactory().wrap(bound);
            entity.setParent(getParent());
            uri = uri + separator + entity.getRelativeURI();
        }

        return uri;
    }

    @Override
    protected RDFNode getType() {
        if (isWildcard()) {
            return Ontology.WILDCARD_ENTITY;
        } else {
            return Ontology.TYPE_VARIABLE_ENTITY;
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private Entity<?> findParent(ExecutableEntity<?> executable) {
        CtExecutableReference<?> executableReference = (CtExecutableReference<?>) executable.getReference();

        if (executable.isDeclarationAvailable()) {
            List<CtTypeReference<?>> parameters = new TypeVariableList(executable.getElement().getFormalTypeParameters());
            if (parameters.contains(getReference())) {
                return executable;
            } else {
                return findParent(executableReference.getDeclaringType());
            }
        } else {
            return findParent(executableReference);
        }
    }

    private Entity<?> findParent(CtExecutableReference<?> executableReference) {
        if (executableReference.getDeclaration() != null) {
            return findParent(getFactory().wrap(executableReference));
        }
        if (isWildcard()) {
            return getFactory().wrap(executableReference);
        }

        Executable executable = ReflectionFactory.getInstance().createActualExecutable(executableReference);
        if (executable == null) {
            return null;
        }
        TypeVariable<?>[] typeParameters = executable.getTypeParameters();
        Class<?> declaringClass = executable.getDeclaringClass();

        for (TypeVariable current : typeParameters) {
            if (current.getName().equals(getReference().getQualifiedName())) {
                return getFactory().wrap(executableReference);
            }
        }

        return findParent(declaringClass);
    }


    private Entity<?> findParent(Class<?> clazz) {
        if (clazz != null) {
            TypeVariable<?>[] typeParameters = clazz.getTypeParameters();

            for (TypeVariable variable : typeParameters) {
                if (variable.getName().equals(getReference().getQualifiedName())) {
                    CtTypeReference<?> reference = ReflectionFactory.getInstance().createTypeReference(clazz);
                    return getFactory().wrap(reference);
                }
            }

            if (clazz.isAnonymousClass()) {
                try {
                    CtExecutableReference<?> reference = ReflectionFactory.getInstance().createMethod(clazz.getEnclosingMethod());
                    return findParent(reference);
                } catch (Exception | Error e) {
                    return null;
                }
            } else {
                return findParent(clazz.getDeclaringClass());
            }
        }

        return null;
    }

    private Entity<?> findParent(CtTypeReference<?> reference) {
        if (reference != null) {
            if (isWildcard()) {
                return getFactory().wrap(reference);
            } else if (reference.getDeclaration() != null) {
                return findParent(reference.getDeclaration());
            } else {
                return findParent(reference.getActualClass());
            }
        }

        return null;
    }

    private Entity<?> findParent(CtType type) {
        if (type != null) {
            List<CtTypeReference<?>> formalTypes = new TypeVariableList(type.getFormalTypeParameters());
            if (formalTypes.contains(getReference())) {
                return getFactory().wrap(type);
            } else {
                CtElement parent;
                try {
                    parent = type.getParent();
                } catch (ParentNotInitializedException e) {
                    parent = null;
                }

                if (parent != null) {
                    CtExecutable<?> executable = parent.getParent(CtExecutable.class);
                    if (executable != null) {
                        Entity<?> result = findParent(executable.getReference());
                        if (result != null) {
                            return result;
                        }
                    }
                }
                return findParent(type.getDeclaringType());
            }
        }

        return null;
    }

    @Override
    public void setParent(Entity<?> context) {

        if (!CodeOntology.processGenerics() || isWildcard()) {
            super.setParent(context);
            return;
        }

        CtReference reference = ((NamedElementEntity) context).getReference();
        String simpleName = getReference().getSimpleName();

        Entity<?> parent = TypeVariableCache.getInstance().getParent(simpleName, context);

        if (parent != null) {
            super.setParent(parent);
            return;
        }

        if (context instanceof GenericDeclarationEntity) {
            parent = findParent((GenericDeclarationEntity<?>) context);
        }

        if (parent == null) {
            if (reference instanceof CtTypeReference<?>) {
                parent = findParent((CtTypeReference) reference);
            } else if (reference instanceof CtExecutableReference) {
                parent = findParent((CtExecutableReference) reference);
            }
        }

        if (parent != null) {
            super.setParent(parent);
            TypeVariableCache.getInstance().putParent(simpleName, context, parent);
        }
    }

    private Entity<?> findParent(GenericDeclarationEntity<?> context) {
        while (context != null) {
            List<TypeVariableEntity> parameters = context.getFormalTypeParameters();
            for (TypeVariableEntity typeVariable : parameters) {
                if (typeVariable.getName().equals(this.getName())) {
                    return context;
                }
            }
            context = (GenericDeclarationEntity<?>) context.getParent(GenericDeclarationEntity.class);
        }
        return null;
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

class TypeVariableCache {
    private static TypeVariableCache instance;
    private Table<String, Entity<?>, Entity<?>> table;
    private int size;
    private static final int ROWS = 16;
    private static final int COLS = 48;
    private static final int MAX_SIZE = (ROWS * COLS) / 2;
    private TypeVariableCache() {
        init();
    }

    public static TypeVariableCache getInstance() {
        if (instance == null) {
            instance = new TypeVariableCache();
        }

        return instance;
    }

    public Entity<?> getParent(String name, Entity<?> context) {
        return table.get(name, context);
    }

    public void putParent(String name, Entity<?> context, Entity<?> parent) {
        handleSize();
        table.put(name, context, parent);
    }

    private void handleSize() {
        size++;
        if (size > MAX_SIZE) {
            init();
        }
    }

    private void init() {
        table = HashBasedTable.create(ROWS, COLS);
        size = 0;
    }
}