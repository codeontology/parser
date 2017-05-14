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

package org.codeontology.build;

import java.io.File;
import java.util.Set;

public abstract class DependenciesLoader<T extends Project> {

    private T project;

    public DependenciesLoader(T project) {
        this.project = project;
    }

    private ClasspathLoader loader = ClasspathLoader.getInstance();

    public abstract void loadDependencies();

    public Set<File> getJarsLoaded() {
        return getLoader().getJarsLoaded();
    }

    public void loadClasspath(String classpath) {
        loader.loadClasspath(classpath);
    }

    public ClasspathLoader getLoader() {
        return loader;
    }

    public T getProject() {
        return project;
    }


}