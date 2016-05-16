package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import spoon.reflect.code.CtStatement;

public class StatementWrapper extends AbstractWrapper<CtStatement> {
    private static final String TAG = "statement";

    public StatementWrapper(CtStatement element) {
        super(element);
    }

    @Override
    public String buildRelativeURI() {
        return "";
    }

    @Override
    protected RDFNode getType() {
        return null;
    }

    @Override
    public void extract() {

    }
}
