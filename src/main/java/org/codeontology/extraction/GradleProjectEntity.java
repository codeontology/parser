package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.buildsystems.gradle.GradleProject;

public class GradleProjectEntity extends ProjectEntity<GradleProject> {
    public GradleProjectEntity(GradleProject project) {
        super(project);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.GRADLE_PROJECT_ENTITY;
    }
}
