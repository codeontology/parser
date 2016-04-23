package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtLocalVariable;

public class LocalVariableWrapper extends Wrapper<CtLocalVariable<?>> {

    private ExecutableWrapper<?> parent;
    private JavaTypeTagger javaTypeTagger;
    private DeclaredByTagger declaredByTagger;

    public LocalVariableWrapper(CtLocalVariable<?> variable) {
        super(variable);
    }

    @Override
    public void extract() {
        setTaggers();
        tagType();
        tagName();
        tagJavaType();
        tagDeclaredBy();
    }

    private void setTaggers() {
        declaredByTagger = new DeclaredByTagger(this);
        javaTypeTagger = new JavaTypeTagger(this);
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
        declaredByTagger.tagDeclaredBy();
    }

    protected void tagJavaType() {
        javaTypeTagger.tagJavaType(this);
    }

    public void setParent(ExecutableWrapper<?> parent) {
        this.parent = parent;
    }

    public ExecutableWrapper<?> getParent() {
        return parent;
    }
}
