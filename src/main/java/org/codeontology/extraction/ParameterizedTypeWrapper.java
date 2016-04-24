package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class ParameterizedTypeWrapper extends TypeWrapper<CtType<?>> {
    private List<CtTypeReference<?>> arguments;

    public ParameterizedTypeWrapper(CtTypeReference reference) {
        super(reference);
        arguments = getReference().getActualTypeArguments();
    }

    @Override
    public String getRelativeURI() {
        arguments = getReference().getActualTypeArguments();
        String uri = getReference().getQualifiedName();
        String argumentsString = "";
        Wrapper<?> parent = getParent();

        for (CtTypeReference<?> argument : arguments) {
            TypeWrapper<?> argumentWrapper = getFactory().wrap(argument);
            argumentWrapper.setParent(parent);
            if (argumentsString.equals("")) {
                argumentsString = argumentWrapper.getRelativeURI();
            } else {
                argumentsString = argumentsString + SEPARATOR + argumentWrapper.getRelativeURI();
            }
        }

        uri = uri + "[" + argumentsString + "]";
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

    public void tagRawType() {
        CoreFactory coreFactory = getReference().getFactory().Core();
        CtTypeReference<?> cloneReference = coreFactory.clone(getReference());
        cloneReference.setActualTypeArguments(new ArrayList<>());
        TypeWrapper rawType = getFactory().wrap(cloneReference);
        getLogger().addTriple(this, Ontology.RAW_TYPE_PROPERTY, rawType);
    }

    public void tagActualTypeArguments() {
        for (int i = 0; i < arguments.size(); i++) {
            TypeArgumentWrapper typeArgument = new TypeArgumentWrapper(arguments.get(i));
            typeArgument.setPosition(i);
            getLogger().addTriple(this, Ontology.ACTUAL_TYPE_ARGUMENT_PROPERTY, typeArgument);
            typeArgument.extract();
        }
    }

    class TypeArgumentWrapper extends TypeWrapper<CtType<?>> {

        private int position = 0;
        private TypeWrapper<?> argument;

        public TypeArgumentWrapper(CtTypeReference reference) {
            super(reference);
            argument = getFactory().wrap(getReference());
            setParent(ParameterizedTypeWrapper.this.getParent());
        }

        @Override
        public void extract() {
            tagType();
            tagJavaType();
            tagPosition();
        }

        private void tagJavaType() {
            getLogger().addTriple(this, Ontology.JAVA_TYPE_PROPERTY, argument);
            if (!argument.isDeclarationAvailable()) {
                argument.extract();
            }
        }

        private void tagPosition() {
            getLogger().addTriple(TypeArgumentWrapper.this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
        }

        @Override
        public String getRelativeURI() {
            return ParameterizedTypeWrapper.this.getRelativeURI() + SEPARATOR + position;
        }

        @Override
        protected RDFNode getType() {
            return Ontology.TYPE_ARGUMENT_CLASS;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void setParent(Wrapper<?> parent) {
            super.setParent(parent);
            argument.setParent(parent);
        }
    }
}
