package org.codeontology.extraction.project;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.build.gradle.GradleProject;

public class GradleProjectEntity extends ProjectEntity<GradleProject> {
    public GradleProjectEntity(GradleProject project) {
        super(project);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.GRADLE_PROJECT_ENTITY;
    }
}
