package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtLambda;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

public class LambdaWrapper extends Wrapper<CtLambda<?>> {
    private ExecutableWrapper<?> parent;
    private static final String TAG = "lambda";


    public LambdaWrapper(CtLambda<?> lambda) {
        super(lambda);
    }

    @Override
    public void extract() {
        tagType();
        tagSourceCode();
        tagFunctionalImplements();
    }

    private void tagFunctionalImplements() {
        Wrapper<?> implementedType = getFactory().wrap(getElement().getType());
        if (implementedType instanceof TypeVariableWrapper) {
            ((TypeVariableWrapper) implementedType).findAndSetParent(parent);
        } else if (implementedType instanceof ArrayWrapper) {
            ((ArrayWrapper) implementedType).setParent(parent.getReference());
        } else if (implementedType instanceof ParameterizedTypeWrapper) {
            ((ParameterizedTypeWrapper) implementedType).setParent(parent.getReference());
        }
        if (!implementedType.isDeclarationAvailable()) {
            implementedType.extract();
        }
        getLogger().addTriple(this, Ontology.IMPLEMENTS_PROPERTY, implementedType);
    }

    @Override
    protected String getRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR +
                getElement().getType().getQualifiedName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.LAMBDA_CLASS;
    }

    public void setParent(ExecutableWrapper<?> executable) {
        this.parent = executable;
    }

    public ExecutableWrapper<?> getParent() {
        return parent;
    }
}
