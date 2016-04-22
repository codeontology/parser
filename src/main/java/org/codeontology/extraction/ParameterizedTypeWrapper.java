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
        String argumentsString = "";

        for (CtTypeReference<?> argument : arguments) {
            TypeWrapper argumentWrapper = getFactory().wrap(argument);
            if (argumentWrapper instanceof TypeVariableWrapper) {
                TypeVariableWrapper typeVariable = (TypeVariableWrapper) argumentWrapper;

                if (parent instanceof CtTypeReference) {
                    typeVariable.findAndSetParent((CtTypeReference) parent);
                } else if (parent instanceof CtExecutableReference) {
                    typeVariable.findAndSetParent((CtExecutableReference) parent);
                }
            } else if (argumentWrapper instanceof ArrayWrapper) {
                ((ArrayWrapper) argumentWrapper).setParent(parent);
            } else if (argumentWrapper instanceof ParameterizedTypeWrapper) {
                ((ParameterizedTypeWrapper) argumentWrapper).setParent(parent);
            }
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

    protected void tagRawType() {
        CoreFactory coreFactory = getReference().getFactory().Core();
        CtTypeReference<?> cloneReference = coreFactory.clone(getReference());
        cloneReference.setActualTypeArguments(new ArrayList<>());
        TypeWrapper rawType = getFactory().wrap(cloneReference);
        getLogger().addTriple(this, Ontology.RAW_TYPE_PROPERTY, rawType);
    }

    protected void tagActualTypeArguments() {
        for (int i = 0; i < arguments.size(); i++) {
            TypeArgumentWrapper typeArgument = new TypeArgumentWrapper(arguments.get(i));
            typeArgument.setPosition(i);
            getLogger().addTriple(this, Ontology.ACTUAL_TYPE_ARGUMENT_PROPERTY, typeArgument);
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
            getLogger().addTriple(this, Ontology.JAVA_TYPE_PROPERTY, argument);
            if (!argument.isDeclarationAvailable()) {
                argument.extract();
            }
        }

        private void tagPosition() {
            getLogger().addTriple(TypeArgumentWrapper.this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
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
            } else if (argument instanceof ArrayWrapper) {
                ((ArrayWrapper) argument).setParent(parent);
            } else if (argument instanceof ParameterizedTypeWrapper) {
                ((ParameterizedTypeWrapper) argument).setParent(parent);
            }
        }
    }
}
