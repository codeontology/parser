package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

import java.lang.reflect.Method;

public class MethodExtractor extends ExecutableExtractor<CtMethod<?>> {
    public MethodExtractor(CtMethod<?> method) {
        super(method);
    }

    public MethodExtractor(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.METHOD_CLASS;
    }

    @Override
    public void extract() {
        super.extract();
        tagReturns();
    }

    protected void tagReturns() {
        if (isDeclarationAvailable()) {
            tagReturnsByReference();
        } else {
            tagReturnsByReflection();
        }
    }

    private void tagReturnsByReference() {
        CtTypeReference<?> reference = ((CtExecutableReference<?>) getReference()).getType();
        Extractor extractor = getFactory().getExtractor(reference);
        addTriple(this, Ontology.RETURNS_PROPERTY, extractor.getResource());
        if (reference.getDeclaration() == null) {
            extractor.extract();
        }
    }

    private void tagReturnsByReflection() {
        CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference());
        try {
            Method method = reference.getActualMethod();
            Class<?> returnType = method.getReturnType();
            String name = returnType.getCanonicalName();
            if (name == null) {
                name = returnType.getSimpleName();
            }
            addTriple(this, Ontology.RETURNS_PROPERTY, getModel().getResource(Ontology.BASE_URI + name));
            Extractor extractor = getFactory().getExtractor(reference.getType());
            if (extractor != null && !extractor.isDeclarationAvailable()) {
                extractor.extract();
            }
        } catch (SpoonClassNotFoundException | NullPointerException e) {
            tagReturnsByReference();
        }
    }
}