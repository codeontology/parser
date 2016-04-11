package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtLocalVariable;

public class LocalVariableExtractor extends TypedElementExtractor<CtLocalVariable<?>> {

    private ExecutableExtractor<?> parent;

    public LocalVariableExtractor(CtLocalVariable<?> variable) {
        super(variable);
    }

    @Override
    public void extract() {
        tagType();
        tagName();
        tagJavaType();
        tagDeclaredBy();
    }

    @Override
    protected String getRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.VARIABLE_CLASS;
    }

    public void tagDeclaredBy() {
        addTriple(this, Ontology.DECLARED_BY_PROPERTY, getParent().getResource());
    }

    public ExecutableExtractor<?> getParent() {
        return parent;
    }

    public void setParent(ExecutableExtractor<?> parent) {
        this.parent = parent;
    }
}
