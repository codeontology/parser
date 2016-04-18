package org.codeontology.buildsystems;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class ClasspathLoader {

    private static ClasspathLoader instance;

    private ClasspathLoader() {

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
        
        try {
            load(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void load(URL url) {
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
        Set<File> jars = new HashSet<>();

        jars.addAll(FileUtils.listFiles(root,
                FileFilterUtils.suffixFileFilter(".jar"),
                TrueFileFilter.INSTANCE));

        for (File jar : jars) {
            load(jar);
        }
    }

    public void loadAllJars(String path) throws IOException {
        loadAllJars(new File(path));
    }

    public void loadClasspath(String classpath) {
        String[] paths = classpath.split(":");
        for (String path : paths) {
            load(path);
        }
    }
}

