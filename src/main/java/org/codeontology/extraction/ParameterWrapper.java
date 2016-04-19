package org.codeontology.extraction;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

public class ParameterWrapper extends Wrapper<CtParameter<?>> {

    private int position;
    private ExecutableWrapper<?> parent;
    private boolean parameterAvailable = true;
    private JavaTypeTagger tagger;

    public ParameterWrapper(CtParameter<?> parameter) {
        super(parameter);
        parameterAvailable = true;
        tagger = new JavaTypeTagger(this);
    }

    public ParameterWrapper(CtTypeReference<?> reference) {
        super(reference);
        parameterAvailable = false;
        if (reference.getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            throw new NullTypeException();
        }
        tagger = new JavaTypeTagger(this);
    }

    @Override
    public void extract() {
        tagType();
        tagJavaType();
        tagPosition();
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagName();
        }
    }

    @Override
    protected String getRelativeURI() {
        return getParent().getRelativeURI() + position;
    }

    public void tagPosition() {
        RDFWriter.addTriple(this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setParent(ExecutableWrapper<?> parent) {
        this.parent = parent;
    }

    public ExecutableWrapper<?> getParent() {
        return this.parent;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PARAMETER_CLASS;
    }

    protected void tagJavaType() {
        tagger.tagJavaType(parent);
    }

    @Override
    public boolean isDeclarationAvailable() {
        return parameterAvailable;
    }
}
