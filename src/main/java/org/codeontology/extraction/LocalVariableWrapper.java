package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtLocalVariable;

public class LocalVariableWrapper extends Wrapper<CtLocalVariable<?>> {

    private ExecutableWrapper<?> parent;
    private JavaTypeTagger tagger;

    public LocalVariableWrapper(CtLocalVariable<?> variable) {
        super(variable);
        tagger = new JavaTypeTagger(this);
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
        return parent.getRelativeURI() + SEPARATOR + getElement().getSimpleName();
    }

    @Override
    protected RDFNode getType() {
        return Ontology.VARIABLE_CLASS;
    }

    public void tagDeclaredBy() {
        RDFWriter.addTriple(this, Ontology.DECLARED_BY_PROPERTY, parent);
    }

    protected void tagJavaType() {
        tagger.tagJavaType(this);
    }

    public void setParent(ExecutableWrapper<?> parent) {
        this.parent = parent;
    }

    public ExecutableWrapper<?> getParent() {
        return parent;
    }
}
