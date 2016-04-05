package org.codeontology.extractors;

import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

public class ExtractorFactory {

    private ExtractorFactory() {

    }

    public static ExtractorFactory getInstance() {
        return new ExtractorFactory();
    }

    public PackageExtractor getExtractor(CtPackage pack) {
        return new PackageExtractor(pack);
    }

    public FieldExtractor getExtractor(CtField<?> field) {
        return new FieldExtractor(field);
    }

    public MethodExtractor getExtractor(CtMethod<?> method) {
        return new MethodExtractor(method);
    }

    public TypeExtractor getExtractor(CtType<?> type) {
        return getExtractor(type.getReference());
    }

    public TypeExtractor getExtractor(CtTypeReference<?> reference) {
        try {
            return new TypeExtractor<>(reference);
        } catch (NullTypeException e) {
            return null;
        }
    }

    public LocalVariableExtractor getExtractor(CtLocalVariable<?> variable) {
        return new LocalVariableExtractor(variable);
    }

    public ParameterExtractor getExtractor(CtParameter<?> parameter) {
        try {
            return new ParameterExtractor(parameter);
        } catch (NullTypeException e) {
            return null;
        }
    }

    public ParameterExtractor getExtractorByTypeReference(CtTypeReference<?> reference) {
        return new ParameterExtractor(reference);
    }

    public ConstructorExtractor getExtractor(CtConstructor<?> constructor) {
        return new ConstructorExtractor(constructor);
    }

    public ExecutableExtractor<?> getExtractor(CtExecutableReference<?> reference) {
        if (reference.isConstructor()) {
            return new ConstructorExtractor(reference);
        } else {
            return new MethodExtractor(reference);
        }
    }
}
