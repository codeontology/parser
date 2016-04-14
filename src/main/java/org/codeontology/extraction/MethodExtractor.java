package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
            Type returnType = method.getGenericReturnType();
            String name = returnType.getTypeName();
            name = name.replaceAll("<|>", SEPARATOR);
            addTriple(this, Ontology.RETURNS_PROPERTY, getModel().getResource(Ontology.WOC + name));
            Extractor extractor = getFactory().getExtractor(reference.getType());
            if (extractor != null && !extractor.isDeclarationAvailable()) {
                extractor.extract();
            }
        } catch (SpoonClassNotFoundException | NullPointerException e) {
            tagReturnsByReference();
        }
    }
}