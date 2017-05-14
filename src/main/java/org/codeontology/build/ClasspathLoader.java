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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class ClasspathLoader {

    private static ClasspathLoader instance;
    private Set<File> classpath;
    private boolean locked;

    private ClasspathLoader() {
        classpath = new HashSet<>();
        locked = false;
    }

    public static ClasspathLoader getInstance() {
        if (instance == null) {
            instance = new ClasspathLoader();
        }
        return instance;
    }

    public void load(String path) {
        load(new File(path));
    }

    public void load(File file) {
        if (file.isDirectory()) {
            loadAllJars(file);
            return;
        }

        if (file.getPath().endsWith(".jar") && !locked) {
            classpath.add(file);
        }

        try {
            load(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void load(URL url) {
        URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> clazz = URLClassLoader.class;
        final Class[] PARAMETERS = new Class[]{URL.class};

        try {
            Method method = clazz.getDeclaredMethod("addURL", PARAMETERS);
            method.setAccessible(true);
            method.invoke(loader, url);
        } catch (Throwable t) {
           System.err.println("Error loading " + url.getPath());
        }
    }

    public void loadAllJars(File root) {
        if (root.isDirectory()) {
            Set<File> jars = new HashSet<>();

            jars.addAll(FileUtils.listFiles(root,
                    FileFilterUtils.suffixFileFilter(".jar"),
                    TrueFileFilter.INSTANCE));

            jars.forEach(this::load);
        }
    }

    public void loadAllJars(String path) {
        loadAllJars(new File(path));
    }

    public void loadClasspath(String classpath) {
        String[] paths = classpath.split(":");
        for (String path : paths) {
            load(path);
        }
    }

    public Set<File> getJarsLoaded() {
        return classpath;
    }

    public void lock() {
        locked = true;
    }

    public void release() {
        locked = false;
    }
}
