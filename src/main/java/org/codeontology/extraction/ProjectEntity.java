package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.buildsystems.Project;

public class ProjectEntity extends AbstractEntity<Project> {
    @Override
    protected String buildRelativeURI() {
        return null;
    }

    @Override
    protected RDFNode getType() {
        return null;
    }

    @Override
    public void extract() {

    }
}
