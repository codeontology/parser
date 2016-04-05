package org.codeontology.extractors;


import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

public class ParameterExtractor extends TypedElementExtractor<CtParameter<?>> {

    private int position;
    private ExecutableExtractor<?> parent;

    public ParameterExtractor(CtParameter<?> parameter) {
        super(parameter);
    }

    public ParameterExtractor(CtTypeReference<?> reference) {
        super(reference);
        if (reference.getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            throw new NullTypeException();
        }
    }

    @Override
    public void extract() {
        tagType();
        tagJavaType();
        tagPosition();
        if (isDeclarationAvailable()) {
            tagName();
            tagComment();
        }
    }

    @Override
    protected String getRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + position;
    }

    public void tagPosition() {
        addStatement(Ontology.getParameterPositionProperty(), getModel().createTypedLiteral(position));
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setParent(ExecutableExtractor<?> parent) {
        this.parent = parent;
    }

    public ExecutableExtractor<?> getParent() {
        return this.parent;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.getParameterIndividual();
    }

    @Override
    public void tagName() {
        if (!isDeclarationAvailable()) {
            super.tagName();
        }
    }
}
