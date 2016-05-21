package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.projects.DefaultProject;

public class DefaultProjectEntity extends ProjectEntity<DefaultProject> {

    public DefaultProjectEntity(DefaultProject project) {
        super(project);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PROJECT_ENTITY;
    }
}
