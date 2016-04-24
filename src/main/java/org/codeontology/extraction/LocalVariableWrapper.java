package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtLocalVariable;

public class LocalVariableWrapper extends Wrapper<CtLocalVariable<?>> {

    public LocalVariableWrapper(CtLocalVariable<?> variable) {
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
        new DeclaredByTagger(this).tagDeclaredBy();
    }

    protected void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType(this.getParent());
    }
}
