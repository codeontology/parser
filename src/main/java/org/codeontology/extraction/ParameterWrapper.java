package org.codeontology.extraction;


import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.commentparser.DocCommentParser;
import org.codeontology.commentparser.ParamTag;
import org.codeontology.commentparser.Tag;
import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public class ParameterWrapper extends Wrapper<CtParameter<?>> {

    private int position;
    private ExecutableWrapper parent;
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
            tagComment();
        }
    }

    @Override
    protected String getRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR  + position;
    }

    public void tagPosition() {
        getLogger().addTriple(this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
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

    @Override
    protected void tagComment() {
        if (parent.isDeclarationAvailable()) {
            String methodComment = parent.getElement().getDocComment();
            if (methodComment != null) {
                DocCommentParser parser = new DocCommentParser(methodComment);
                List<Tag> tags = parser.getTags("@param");
                for (Tag tag : tags) {
                    ParamTag paramTag = (ParamTag) tag;
                    if (paramTag.getParameterName().equals(getElement().getSimpleName())) {
                        Literal comment = getModel().createLiteral(paramTag.getParameterComment());
                        getLogger().addTriple(this, Ontology.COMMENT_PROPERTY, comment);
                        break;
                    }
                }
            }
        }
    }
}
