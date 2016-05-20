package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Literal;
import org.codeontology.Ontology;
import org.codeontology.buildsystems.Project;

import java.util.Collection;

public abstract class ProjectEntity<T extends Project> extends AbstractEntity<T> {

    public ProjectEntity(T project) {
        super(project);
    }

    @Override
    protected String buildRelativeURI() {
        return getElement().getName() + SEPARATOR +
                getElement().getSubProjects().hashCode() + SEPARATOR +
                getElement().getBuildFileContent().hashCode();
    }

    @Override
    public void extract() {
        tagType();
        tagBuildFile();
        tagSubProjects();
        tagName();
    }

    public void tagName() {
        String name = getElement().getName();
        Literal label = getModel().createTypedLiteral(name);
        getLogger().addTriple(this, Ontology.RDFS_LABEL_PROPERTY, label);
    }


    public void tagSubProjects() {
        Collection<Project> subProjects = getElement().getSubProjects();
        for (Project subProject : subProjects) {
            ProjectVisitor visitor = new ProjectVisitor();
            subProject.accept(visitor);
            ProjectEntity<?> entity = visitor.getLastEntity();
            getLogger().addTriple(this, Ontology.SUBPROJECT_PROPERTY, entity);
            entity.extract();
        }
    }

    public void tagBuildFile() {
        if (getElement().getBuildFile() != null) {
            String buildFileContent = getElement().getBuildFileContent();
            Literal buildFileLiteral = getModel().createTypedLiteral(buildFileContent);
            getLogger().addTriple(this, Ontology.BUILD_FILE_PROPERTY, buildFileLiteral);
        }
    }
}
