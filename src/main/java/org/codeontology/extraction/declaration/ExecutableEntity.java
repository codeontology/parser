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

import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import org.codeontology.extraction.Entity;
import org.codeontology.extraction.NamedElementEntity;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.statement.StatementEntity;
import org.codeontology.extraction.support.*;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.*;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;
import spoon.support.reflect.reference.CtFieldReferenceImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ExecutableEntity<E extends CtExecutable<?> & CtTypeMember & CtGenericElement>
        extends NamedElementEntity<E> implements ModifiableEntity<E>, MemberEntity<E>, BodyHolderEntity<E> {

    private Set<ExecutableEntity<?>> executables;
    private Set<TypeEntity<?>> requestedTypes;
    private Set<LambdaEntity> lambdas;
    private Set<AnonymousClassEntity> anonymousClasses;
    private Set<LocalVariableEntity> localVariables;
    private Set<FieldEntity> fields;
    private List<ParameterEntity> parameters;

    public ExecutableEntity(E executable) {
        super(executable);
        initSets();
    }

    public ExecutableEntity(CtExecutableReference<?> reference) {
        super(reference);
        initSets();
    }

    private void initSets() {
        executables = new HashSet<>();
        requestedTypes = new HashSet<>();
        lambdas = new HashSet<>();
        anonymousClasses = new HashSet<>();
        localVariables = new HashSet<>();
        fields = new HashSet<>();
    }

    @Override
    public String buildRelativeURI() {
        String uri = getReference().toString();
        uri = uri.replaceAll(", |#", SEPARATOR);
        return uri;
    }

    @Override
    public void extract() {
        tagName();
        tagLabel();
        tagType();
        tagDeclaringElement();
        tagParameters();
        tagModifiers();
        tagVarArgs();
        if (isDeclarationAvailable()) {
            processStatements();
            tagRequestedTypes();
            tagExecutables();
            tagRequestedFields();
            tagLocalVariables();
            tagLambdas();
            tagAnonymousClasses();
            tagAnnotations();
            tagComment();
            tagSourceCode();
            tagThrows();
            tagBody();
        }
    }

    public Entity<?> getDeclaringElement() {
        CtExecutableReference<?> reference = (CtExecutableReference<?>) getReference();
        return getFactory().wrap(reference.getDeclaringType());
    }

    public void tagDeclaringElement() {
        new DeclaringElementTagger(this).tagDeclaredBy();
    }

    public void tagModifiers() {
        new ModifiableTagger(this).tagModifiers();
    }

    public List<Modifier> getModifiers() {
        if (isDeclarationAvailable()) {
            return Modifier.asList(getElement().getModifiers());
        } else {
            CtExecutableReference<?> reference = (CtExecutableReference<?>) getReference();
            Executable executable = ReflectionFactory.getInstance().createActualExecutable(reference);
            if (executable != null) {
                int modifiersCode = executable.getModifiers();
                return Modifier.asList(modifiersCode);
            }
            return new ArrayList<>();
        }
    }

    public void tagParameters() {
        List<ParameterEntity> parameters = getParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            ParameterEntity parameter = parameters.get(i);
            parameter.setParent(this);
            parameter.setPosition(i);
            getLogger().addTriple(this, Ontology.PARAMETER_PROPERTY, parameter);
            parameter.extract();
        }
    }

    public List<ParameterEntity> getParameters() {
        if (parameters == null) {
            setParameters();
        }

        return parameters;
    }

    private void setParameters() {
        if (isDeclarationAvailable()) {
            List<CtParameter<?>> parameterList = getElement().getParameters();
            parameters = parameterList.stream()
                    .map(getFactory()::wrap)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            List<CtTypeReference<?>> references = ((CtExecutableReference<?>) getReference()).getParameters();
            parameters = references.stream()
                    .map(getFactory()::wrapByTypeReference)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public void tagThrows() {
        Set<CtTypeReference<? extends Throwable>> thrownTypes = getElement().getThrownTypes();
        for (CtTypeReference<? extends Throwable> current : thrownTypes) {
            TypeEntity<?> thrownType = getFactory().wrap(current);
            thrownType.setParent(this);
            getLogger().addTriple(this, Ontology.THROWS_PROPERTY, thrownType);
        }
    }

    protected void processStatements() {
        addRequestedTypes(new HashSet<>(getElement().getThrownTypes()));

        CtExecutable executable = getElement();
        CtBlock<?> body = executable.getBody();

        List<CtStatement> statements;
        try {
            statements = body.getStatements();
        } catch (NullPointerException e) {
            return;
        }

        for (CtStatement statement : statements) {
            if (createsAnonymousClass(statement) || statement instanceof CtClass) {
                addAnonymousClasses(statement);
            } else {
                addRequestedTypes(statement.getReferencedTypes());
                addInvocations(statement);
                addRequestedFields(statement);
                addLocalVariables(statement);
                addLambdas(statement);
            }
            if (statement instanceof CtReturn<?>) {
                tagReturnsVariable((CtReturn<?>) statement);
            }
        }

    }

    public void addAnonymousClasses(CtStatement statement) {
        List<CtNewClass<?>> newClasses = statement.getElements(element -> element != null);
        for (CtNewClass<?> newClass : newClasses) {
            AnonymousClassEntity<?> anonymousClass = new AnonymousClassEntity<>(newClass.getAnonymousClass());
            anonymousClass.setParent(this);
            anonymousClasses.add(anonymousClass);
        }
    }

    public void tagAnonymousClasses() {
        for (AnonymousClassEntity<?> anonymousClass : anonymousClasses) {
            getLogger().addTriple(this, Ontology.CONSTRUCTS_PROPERTY, anonymousClass);
            anonymousClass.extract();
            anonymousClass.getRequestedResources().forEach(this::tagRequests);
        }
    }

    public void addInvocations(CtStatement statement) {
        List<CtExecutableReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtExecutableReferenceImpl.class));

        for (CtExecutableReference<?> reference : references) {
            CtExecutable<?> executable = reference.getDeclaration();
            if (executable instanceof CtLambda<?>) {
                LambdaEntity lambda = getFactory().wrap((CtLambda<?>) executable);
                lambda.setParent(this);
                lambdas.add(lambda);
            } else if (!(reference.getParent() instanceof CtExecutableReferenceExpression<?, ?>)) {
                executables.add(getFactory().wrap(reference));
            }
        }
    }

    public void tagLambdas() {
        for (LambdaEntity lambda : lambdas) {
            tagRequests(lambda);
            lambda.extract();
        }
    }

    public void addLambdas(CtStatement statement) {
        List<CtLambda<?>> lambdas = statement.getElements(element -> element != null);
        for (CtLambda<?> lambda : lambdas) {
            LambdaEntity lambdaEntity =  getFactory().wrap(lambda);
            lambdaEntity.setParent(this);
            this.lambdas.add(lambdaEntity);
        }
    }

    public void addRequestedFields(CtStatement statement) {
        List<CtFieldReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtFieldReferenceImpl.class));
        references.stream()
                .map(getFactory()::wrap)
                .forEach(fields::add);
    }

    public void tagRequestedFields() {
        for (FieldEntity field : fields) {
            tagRequests(field);
            field.follow();
        }
    }

    public void addRequestedTypes(Set<CtTypeReference<?>> types) {
        for (CtTypeReference<?> reference : types) {
            if (!(reference instanceof CtImplicitTypeReference<?>)) {
                TypeEntity<?> type = getFactory().wrap(reference);
                if (type != null) {
                    type.setParent(this);
                    requestedTypes.add(type);
                }
            }
        }
    }

    public void tagRequestedTypes() {
        for (TypeEntity<?> type : requestedTypes) {
            tagRequests(type);
            type.follow();
        }
    }


    private boolean createsAnonymousClass(CtStatement statement) {
        List<CtNewClass> newClasses = statement.getElements(element -> element != null);
        return !newClasses.isEmpty();
    }

    public void addLocalVariables(CtStatement statement) {
        List<CtLocalVariableReference<?>> references = statement.getReferences(new ReferenceTypeFilter<>(CtLocalVariableReferenceImpl.class));

        for (CtLocalVariableReference<?> reference : references) {
            CtLocalVariable variable = reference.getDeclaration();
            if (variable != null) {
                LocalVariableEntity localVariable = getFactory().wrap(variable);
                localVariable.setParent(this);
                localVariables.add(localVariable);
            }
        }
    }

    public void tagLocalVariables() {
        for (LocalVariableEntity variable : localVariables) {
            tagRequests(variable);
            variable.extract();
        }
    }

    public void tagExecutables() {
        for (ExecutableEntity<?> executable : executables) {
            tagRequests(executable);
            if (executable instanceof ConstructorEntity) {
                tagConstructs(executable);
            }
            executable.follow();
        }
    }

    public void tagConstructs(ExecutableEntity<?> executable) {
        Entity<?> declaringType = executable.getDeclaringElement();
        getLogger().addTriple(this, Ontology.CONSTRUCTS_PROPERTY, declaringType);
        declaringType.follow();
    }

    public void tagRequests(Entity<?> requested) {
        getLogger().addTriple(this, Ontology.REFERENCES_PROPERTY, requested);
    }

    public void tagReturnsVariable(CtReturn<?> returnStatement) {
        CtExpression<?> returned = returnStatement.getReturnedExpression();
        if (returned instanceof CtVariableAccess<?>) {
            CtVariableReference<?> reference = ((CtVariableAccess<?>) returned).getVariable();
            if (reference == null) {
                return;
            }

            CtVariable<?> variable = reference.getDeclaration();
            if (variable != null) {
                Entity<?> entity = getFactory().wrap(variable);
                if (entity != null) {
                    entity.setParent(this);
                    getLogger().addTriple(this, Ontology.RETURNS_VAR_PROPERTY, entity);
                }
            }
        }
    }

    public List<Entity<?>> getRequestedResources() {
        List<Entity<?>> requestedResources = new ArrayList<>();

        requestedResources.addAll(executables);
        requestedResources.addAll(fields);
        requestedResources.addAll(requestedTypes);

        return requestedResources;
    }

    public void tagVarArgs() {
        List<ParameterEntity> parameters = getParameters();
        int size = parameters.size();
        boolean value = false;
        if (size != 0) {
            ParameterEntity last = parameters.get(size - 1);
            if (last.isDeclarationAvailable()) {
                value = last.getElement().isVarArgs();
            } else {
                CtExecutableReference<?> reference = (CtExecutableReference<?>) getReference();
                Executable executable = ReflectionFactory.getInstance().createActualExecutable(reference);
                if (executable != null) {
                    value = executable.isVarArgs();
                }
            }
        }
        getLogger().addTriple(this, Ontology.VAR_ARGS_PROPERTY, getModel().createTypedLiteral(value));
    }

    public StatementEntity<?> getBody() {
        CtExecutable executable = getElement();
        CtBlock<?> body = executable.getBody();
        if (body != null) {
            StatementEntity<?> bodyEntity = getFactory().wrap(body);
            bodyEntity.setParent(this);
            return bodyEntity;
        }

        return null;
    }

    public void tagBody() {
        if (CodeOntology.processStatements()) {
            new BodyTagger(this).tagBody();
        }
    }
}