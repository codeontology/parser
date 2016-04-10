package org.codeontology.extractors;

import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.code.CtLambda;
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
        TypeExtractor<?> extractor = null;

        if (reference.getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            return null;
        }

        TypeEntity entity = TypeEntity.getEntity(reference);
        if (entity == null) {
            return null;
        }

        switch (entity) {
            case CLASS:
                extractor = new ClassExtractor<>(reference);
                break;
            case INTERFACE:
                extractor = new InterfaceExtractor(reference);
                break;
            case ENUM:
                extractor = new EnumExtractor<>(reference);
                break;
            case ANNOTATION:
                extractor = new AnnotationExtractor(reference);
                break;
            case PRIMITIVE:
                extractor = new PrimitiveTypeExtractor(reference);
                break;
            case GENERIC:
                extractor = new GenericExtractor(reference);
        }
        return extractor;
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

    public LambdaExtractor getExtractor(CtLambda<?> lambda) {
        return new LambdaExtractor(lambda);
    }
}
