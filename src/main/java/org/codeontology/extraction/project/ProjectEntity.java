/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

package org.codeontology.extraction.project;

import com.hp.hpl.jena.rdf.model.Literal;
import org.codeontology.Ontology;
import org.codeontology.extraction.AbstractEntity;
import org.codeontology.extraction.Entity;
import org.codeontology.build.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

public abstract class ProjectEntity<T extends Project> extends AbstractEntity<T> {

    public ProjectEntity(T project) {
        super(project);
    }

    @Override
    protected String buildRelativeURI() {
        String code;
        if (getElement().getBuildFile() == null) {
            try {
                String[] names = Files.walk(Paths.get(getElement().getPath()))
                        .filter(path -> path.toFile().getPath().endsWith(".java"))
                        .map(path -> path.toFile().getName())
                        .toArray(String[]::new);
                Arrays.sort(names);
                code = String.valueOf(Arrays.hashCode(names));
            } catch (IOException e) {
                code = "0";
            }
        } else {
            String[] subProjects = getElement().getSubProjects().stream().map(Project::getName).toArray(String[]::new);
            Arrays.sort(subProjects);
            int buildFileCode = getElement().getBuildFileContent().hashCode();
            code = String.valueOf(Arrays.hashCode(subProjects)) + SEPARATOR + String.valueOf(buildFileCode);
        }

        return getPrefix() + getElement().getName() + SEPARATOR + code;
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
            entity.setParent(this);
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

    private String getPrefix() {
        StringBuilder builder = new StringBuilder();
        Entity<?> current = this;
        while (current.getParent() != null) {
            Entity<?> parent = current.getParent();
            if (parent instanceof ProjectEntity<?>) {
                builder.append(((ProjectEntity<?>) parent).getElement().getName());
                builder.append(SEPARATOR);
            }
            current = parent;
        }

        return builder.toString();
    }
}