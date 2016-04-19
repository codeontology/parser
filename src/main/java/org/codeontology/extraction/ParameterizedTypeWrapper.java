package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class ParameterizedTypeWrapper extends TypeWrapper<CtType<?>> {

    private CtReference parent;
    List<CtTypeReference<?>> arguments;

    public ParameterizedTypeWrapper(CtTypeReference reference) {
        super(reference);
        arguments = getReference().getActualTypeArguments();
    }

    @Override
    public String getRelativeURI() {
        arguments = getReference().getActualTypeArguments();
        String uri = getReference().getQualifiedName();

        for (CtTypeReference<?> argument : arguments) {
            uri = uri + SEPARATOR + argument;
        }

        uri = uri.replace(" ", "_");

        return uri;
    }

    @Override
    public void extract() {
        tagType();
        tagRawType();
        tagActualTypeArguments();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PARAMETERIZED_TYPE_CLASS;
    }

    protected void tagRawType() {
        CoreFactory coreFactory = getReference().getFactory().Core();
        CtTypeReference<?> cloneReference = coreFactory.clone(getReference());
        cloneReference.setActualTypeArguments(new ArrayList<>());
        TypeWrapper rawType = getFactory().wrap(cloneReference);
        RDFWriter.addTriple(this, Ontology.RAW_TYPE_PROPERTY, rawType);
    }

    protected void tagActualTypeArguments() {
        for (int i = 0; i < arguments.size(); i++) {
            TypeArgumentWrapper typeArgument = new TypeArgumentWrapper(arguments.get(i));
            typeArgument.setPosition(i);
            RDFWriter.addTriple(this, Ontology.ACTUAL_TYPE_ARGUMENT_PROPERTY, typeArgument);
            typeArgument.extract();
        }
    }

    public void setParent(CtReference parent) {
        this.parent = parent;
    }

    class TypeArgumentWrapper extends TypeWrapper<CtType<?>> {

        private int position = 0;
        private TypeWrapper argument;

        public TypeArgumentWrapper(CtTypeReference reference) {
            super(reference);
            argument = getFactory().wrap(getReference());
            handleGenericArgument();
        }

        @Override
        public void extract() {
            tagType();
            tagJavaType();
            tagPosition();
        }

        private void tagJavaType() {
            RDFWriter.addTriple(this, Ontology.JAVA_TYPE_PROPERTY, argument);
            if (!argument.isDeclarationAvailable()) {
                argument.extract();
            }
        }

        private void tagPosition() {
            RDFWriter.addTriple(TypeArgumentWrapper.this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
        }

        @Override
        protected String getRelativeURI() {
            return ParameterizedTypeWrapper.this.getRelativeURI() + SEPARATOR + position;
        }

        @Override
        protected RDFNode getType() {
            return Ontology.TYPE_ARGUMENT_CLASS;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        private void handleGenericArgument() {
            if (argument instanceof TypeVariableWrapper) {
                TypeVariableWrapper typeVariable = (TypeVariableWrapper) argument;

                if (parent instanceof CtTypeReference) {
                    typeVariable.findAndSetParent((CtTypeReference) parent);
                } else if (parent instanceof CtExecutableReference) {
                    typeVariable.findAndSetParent((CtExecutableReference) parent);
                }
            }
        }
    }
}
