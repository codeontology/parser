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

import org.codeontology.extraction.EntityFactory;
import org.codeontology.build.DefaultProject;
import org.codeontology.build.gradle.AndroidProject;
import org.codeontology.build.gradle.GradleProject;
import org.codeontology.build.maven.MavenProject;

public class ProjectVisitor {

    private ProjectEntity<?> lastEntity;

    public void visit(DefaultProject project) {
        lastEntity = EntityFactory.getInstance().wrap(project);
    }

    public void visit(GradleProject project) {
        lastEntity = EntityFactory.getInstance().wrap(project);
    }

    public void visit(MavenProject project) {
        lastEntity = EntityFactory.getInstance().wrap(project);
    }

    public void visit(AndroidProject project) {
        lastEntity = EntityFactory.getInstance().wrap(project);
    }

    public ProjectEntity<?> getLastEntity() {
        return lastEntity;
    }
}