package org.codeontology.extraction.project;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.projects.gradle.AndroidProject;

public class AndroidGradleProjectEntity extends GradleProjectEntity {
    public AndroidGradleProjectEntity(AndroidProject project) {
        super(project);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.ANDROID_PROJECT_ENTITY;
    }
}
