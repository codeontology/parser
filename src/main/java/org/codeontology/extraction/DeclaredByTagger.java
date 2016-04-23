package org.codeontology.extraction;

import org.codeontology.Ontology;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;

public class DeclaredByTagger {

    private Wrapper<? extends CtTypedElement> wrapper;
    private Wrapper parent;

    public DeclaredByTagger(ExecutableWrapper<? extends CtExecutable> executable) {
        this.wrapper = executable;
        CtExecutableReference<?> reference = (CtExecutableReference<?>) executable.getReference();
        parent = WrapperFactory.getInstance().wrap(reference.getDeclaringType());
    }

    public DeclaredByTagger(LocalVariableWrapper localVariable) {
        this.wrapper = localVariable;
        CtExecutableReference<?> reference = (CtExecutableReference<?>) localVariable.getParent().getReference();
        parent = WrapperFactory.getInstance().wrap(reference);
    }

    public DeclaredByTagger(FieldWrapper field) {
        this.wrapper = field;
        CtFieldReference<?> reference = (CtFieldReference<?>) field.getReference();
        parent = WrapperFactory.getInstance().wrap(reference.getDeclaringType());
    }

    public void tagDeclaredBy() {
        RDFLogger.getInstance().addTriple(wrapper, Ontology.DECLARED_BY_PROPERTY, parent);
    }
}
