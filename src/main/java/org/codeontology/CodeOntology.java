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

package org.codeontology;

import com.martiansoftware.jsap.JSAPException;
import org.codeontology.build.DependenciesLoader;
import org.codeontology.build.Project;
import org.codeontology.build.ProjectFactory;
import org.codeontology.extraction.JarProcessor;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.SourceProcessor;
import org.codeontology.extraction.project.ProjectEntity;
import org.codeontology.extraction.project.ProjectVisitor;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import spoon.Launcher;
import spoon.compiler.ModelBuildingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class CodeOntology {
    private static CodeOntology codeOntology;
    private static int status = 0;
    private boolean downloadDependencies;
    private CodeOntologyArguments arguments;
    private Launcher spoon;
    private boolean exploreJarsFlag;
    private Project project;
    private ProjectEntity<?> projectEntity;
    private DependenciesLoader<? extends Project> loader;
    private PeriodFormatter formatter;
    private int tries;
    private String[] directories = {"test", "examples", "debug", "androidTest", "samples", "sample", "example", "demo", ".*test.*", ".*demo.*", ".*sample.*", ".*example.*", "app"};
    public static final String SUFFIX = ".codeontology";

    private CodeOntology(String[] args) {
        try {
            spoon = new Launcher();
            arguments = new CodeOntologyArguments(args);
            exploreJarsFlag = arguments.exploreJars() || (arguments.getJarInput() != null);
            ReflectionFactory.getInstance().setParent(spoon.createFactory());
            RDFLogger.getInstance().setOutputFile(arguments.getOutput());
            downloadDependencies = arguments.downloadDependencies();
            formatter = new PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix(" h ")
                    .appendMinutes()
                    .appendSuffix(" min ")
                    .appendSeconds()
                    .appendSuffix(" s ")
                    .appendMillis()
                    .appendSuffix(" ms")
                    .toFormatter();

            setUncaughtExceptionHandler();

        } catch (JSAPException e) {
            System.out.println("Could not process arguments");
        }
    }

    public static void main(String[] args) {
        codeOntology = new CodeOntology(args);
        try {
            codeOntology.processSources();
            codeOntology.processProjectStructure();
            codeOntology.processJars();
        } catch (Exception | Error e) {
            codeOntology.handleFailure(e);
        }
        codeOntology.postCompletionTasks();
        exit(status);
    }

    private void processSources() {
        try {
            if (isInputSet()) {
                System.out.println("Running on " + getArguments().getInput());

                project = ProjectFactory.getInstance().getProject(getArguments().getInput());

                loadDependencies();

                if (!getArguments().doNotExtractTriples()) {
                    spoon();
                    extractAllTriples();
                }
            }
        } catch (Exception e) {
            handleFailure(e);
        }
    }

    private void processProjectStructure() {
        if (getArguments().extractProjectStructure() && project != null) {
            getProjectEntity().extract();
            RDFLogger.getInstance().writeRDF();
        }
    }

    public void handleFailure(Throwable t) {
        System.out.println("It was a good plan that went awry.");
        if (t != null) {
            if (t.getMessage() != null) {
                System.out.println(t.getMessage());
            }
            if (getArguments().stackTraceMode()) {
                t.printStackTrace();
            }
        }
        status = -1;
    }

    private void spoon() {
        checkInput();
        try {
            long start = System.currentTimeMillis();
            spoon.addInputResource(getArguments().getInput());
            System.out.println("Building model...");
            spoon.buildModel();
            long end = System.currentTimeMillis();
            Period period = new Period(start, end);
            System.out.println("Model built successfully in " + formatter.print(period));

        } catch (ModelBuildingException e) {
            if (getArguments().removeTests() && tries < directories.length) {
                boolean result;
                do {
                    result = removeDirectoriesByName(directories[tries]);
                    tries++;
                } while (!result && tries < directories.length);

                if (result) {
                    spoon = new Launcher();
                    spoon();
                    return;
                }
            }
            throw e;
        }
    }

    private void loadDependencies() {
        long start = System.currentTimeMillis();
        loader = project.getLoader();
        loader.loadDependencies();

        String classpath = getArguments().getClasspath();

        if (classpath != null) {
            loader.loadClasspath(classpath);
        }
        long end = System.currentTimeMillis();
        System.out.println("Dependencies loaded in " + formatter.print(new Period(start, end)) + ".");
    }

    private void extractAllTriples() {
        long start = System.currentTimeMillis();

        System.out.println("Extracting triples...");
        spoon.addProcessor(new SourceProcessor());
        spoon.process();
        RDFLogger.getInstance().writeRDF();

        long end = System.currentTimeMillis();

        Period period = new Period(start, end);
        System.out.println("Triples extracted successfully in " + formatter.print(period) + ".");
        spoon = new Launcher();
    }

    private void processJars() {
        long start = System.currentTimeMillis();
        String path = getArguments().getJarInput();
        if (path != null) {
            JarProcessor processor = new JarProcessor(path);
            processor.process();
        }

        if (getArguments().exploreJars() && loader != null) {
            Set<File> jars = loader.getJarsLoaded();
            for (File jar : jars) {
                new JarProcessor(jar).process();
            }

            long end = System.currentTimeMillis();
            Period period = new Period(start, end);
            System.out.println("Jar files processed successfully in " + formatter.print(period) + ".");
        }
    }

    private void postCompletionTasks() {
        try {
            scheduleShutdownTask();
            restore();
        } catch (IOException e) {
            handleFailure(e);
        }
    }

    private void restore() throws IOException {
        String root = getArguments().getInput();
        Files.walk(Paths.get(root))
                .map(Path::toFile)
                .filter(file -> file.getAbsolutePath().endsWith(SUFFIX))
                .forEach(this::restore);
    }

    private void restore(File file) {
        File original = removeSuffix(file);
        boolean success = true;
        if (original.exists()) {
            success = original.delete();
        }
        success = success && file.renameTo(original);

        if (!success) {
            showWarning("Could not restore file " + file.getPath());
        }
    }

    private File removeSuffix(File suffixed) {
        int suffixLength = SUFFIX.length();
        String path = suffixed.getPath();
        StringBuilder builder = new StringBuilder(path);
        int index = builder.lastIndexOf(SUFFIX);
        builder.replace(index, index + suffixLength, "");
        return new File(builder.toString());
    }

    private void scheduleShutdownTask() {
        if (getInstance().getArguments().shutdownFlag()) {
            Thread shutdownThread = new Thread(() -> {
                try {
                    System.out.println("Shutting down...");
                    ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "sleep 3; shutdown -h now");
                    processBuilder.start();
                } catch (Exception e) {
                    System.out.println("Shutdown failed");
                }
            });
            Runtime.getRuntime().addShutdownHook(shutdownThread);
        }
    }

    private void checkInput() {
        File input = new File(getArguments().getInput());
        if (!input.exists()) {
            System.out.println("File " + input.getPath() + " doesn't seem to exist.");
            System.exit(-1);
        }
        if (!input.canRead() && !input.setReadable(true)) {
            System.out.println("File " + input.getPath() + " doesn't seem to be readable.");
            System.exit(-1);
        }
    }

    public static CodeOntology getInstance() {
        return codeOntology;
    }

    public CodeOntologyArguments getArguments() {
        return arguments;
    }

    public static boolean downloadDependencies() {
        if (codeOntology == null) {
            return true;
        }
        return getInstance().downloadDependencies;
    }

    public static void signalDependenciesDownloaded() {
        getInstance().downloadDependencies = true;
    }

    public static boolean verboseMode() {
        return getInstance().getArguments().verboseMode();
    }

    public static boolean isJarExplorationEnabled() {
        return getInstance().exploreJarsFlag;
    }

    private boolean isInputSet() {
        return getArguments().getInput() != null;
    }

    private boolean removeDirectoriesByName(String name) {
        try {
            Path[] tests = Files.walk(Paths.get(getArguments().getInput()))
                    .filter(path -> match(path, name) && path.toFile().isDirectory())
                    .toArray(Path[]::new);

            if (tests.length == 0) {
                return false;
            }

            for (Path testPath : tests) {
                System.out.println("Ignoring sources in " + testPath.toFile().getAbsolutePath());
                Files.walk(testPath)
                        .filter(path -> path.toFile().getAbsolutePath().endsWith(".java"))
                        .forEach(path -> path.toFile().renameTo(
                                new File(path.toFile().getPath() + SUFFIX))
                        );
            }
        } catch (IOException e) {
            showWarning(e.getMessage());
        }

        return true;
    }

    private boolean match(Path path, String name) {
        if (!name.contains("*")) {
           return path.toFile().getName().equals(name);
        } else {
            Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            return pattern.matcher(path.toFile().getName()).matches();
        }
    }

    public static void showWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    private void setUncaughtExceptionHandler() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) ->  exit(-1));
    }

    private static void exit(final int status) {
        try {
            // setup a timer, so if nice exit fails, the nasty exit happens
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Runtime.getRuntime().halt(status);
                }
            }, 30000);

            // try to exit nicely
            System.exit(status);

        } catch (Throwable t) {
            try {
                Thread.sleep(30000);
                Runtime.getRuntime().halt(status);
            } catch (Exception | Error e) {
                Runtime.getRuntime().halt(status);
            }
        }

        Runtime.getRuntime().halt(status);
    }

    public static ProjectEntity<?> getProject() {
        return codeOntology.getProjectEntity();
    }

    public static boolean extractProjectStructure() {
        return codeOntology.getArguments().extractProjectStructure();
    }

    public ProjectEntity<?> getProjectEntity() {
        if (projectEntity == null) {
            ProjectVisitor visitor = new ProjectVisitor();
            project.accept(visitor);
            projectEntity = visitor.getLastEntity();
        }

        return projectEntity;
    }

    public static boolean processStatements() {
        return codeOntology.getArguments().processStatements();
    }

    public static boolean processExpressions() {
        return codeOntology.getArguments().processExpressions();
    }

    public static boolean processGenerics() {
        // return codeOntology.getArguments().processGenerics();
        return true;
    }
}