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

package org.codeontology.build.gradle;

import org.codeontology.extraction.project.ProjectVisitor;
import org.codeontology.build.DependenciesLoader;

import java.io.File;

public class AndroidProject extends GradleProject {

    private AndroidLoader loader;

    public AndroidProject(File project) {
        super(project);
    }

    @Override
    public DependenciesLoader<? extends GradleProject> getLoader() {
        if (loader == null) {
            loader = new AndroidLoader(this);
        }
        return loader;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}