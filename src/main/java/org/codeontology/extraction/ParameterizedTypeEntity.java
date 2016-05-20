package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class ParameterizedTypeEntity extends TypeEntity<CtType<?>> {
    private List<CtTypeReference<?>> arguments;

    public ParameterizedTypeEntity(CtTypeReference reference) {
        super(reference);
        arguments = getReference().getActualTypeArguments();
    }

    @Override
    public String buildRelativeURI() {
        arguments = getReference().getActualTypeArguments();
        String uri = getReference().getQualifiedName();
        String argumentsString = "";
        Entity<?> parent = getParent();

        for (CtTypeReference<?> argument : arguments) {
            TypeEntity<?> argumentEntity = getFactory().wrap(argument);
            argumentEntity.setParent(parent);
            if (argumentsString.equals("")) {
                argumentsString = argumentEntity.getRelativeURI();
            } else {
                argumentsString = argumentsString + SEPARATOR + argumentEntity.getRelativeURI();
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
        return Ontology.PARAMETERIZED_TYPE_ENTITY;
    }

    public void tagRawType() {
        CtTypeReference<?> cloneReference = ReflectionFactory.getInstance().clone(getReference());
        cloneReference.setActualTypeArguments(new ArrayList<>());
        TypeEntity rawType = getFactory().wrap(cloneReference);
        getLogger().addTriple(this, Ontology.RAW_TYPE_PROPERTY, rawType);
    }

    public void tagActualTypeArguments() {
        for (int i = 0; i < arguments.size(); i++) {
            TypeArgumentEntity typeArgument = new TypeArgumentEntity(arguments.get(i));
            typeArgument.setPosition(i);
            getLogger().addTriple(this, Ontology.ACTUAL_TYPE_ARGUMENT_PROPERTY, typeArgument);
            typeArgument.extract();
        }
    }

    @Override
    public void follow() {
        if (EntityRegister.getInstance().add(this))  {
            extract();
        }
    }

    class TypeArgumentEntity extends TypeEntity<CtType<?>> {

        private int position = 0;
        private TypeEntity<?> argument;

        public TypeArgumentEntity(CtTypeReference reference) {
            super(reference);
            argument = getFactory().wrap(getReference());
            setParent(ParameterizedTypeEntity.this.getParent());
        }

        @Override
        public void extract() {
            tagType();
            tagJavaType();
            tagPosition();
        }

        private void tagJavaType() {
            getLogger().addTriple(this, Ontology.JAVA_TYPE_PROPERTY, argument);
            argument.follow();
        }

        private void tagPosition() {
            getLogger().addTriple(TypeArgumentEntity.this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
        }

        @Override
        public String buildRelativeURI() {
            return ParameterizedTypeEntity.this.getRelativeURI() + SEPARATOR + position;
        }

        @Override
        protected RDFNode getType() {
            return Ontology.TYPE_ARGUMENT_ENTITY;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void setParent(Entity<?> parent) {
            super.setParent(parent);
            argument.setParent(parent);
        }
    }
}
