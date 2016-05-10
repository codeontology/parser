package org.codeontology;

import com.martiansoftware.jsap.JSAPException;
import org.apache.commons.io.FileUtils;
import org.codeontology.buildsystems.DependenciesLoader;
import org.codeontology.buildsystems.LoaderFactory;
import org.codeontology.extraction.JarProcessor;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.SourceProcessor;
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

public class CodeOntology {
    private static CodeOntology launcher;
    private boolean downloadDependencies;
    private CodeOntologyArguments arguments;
    private Launcher spoon;
    private boolean exploreJarsFlag;
    private DependenciesLoader loader;
    private PeriodFormatter formatter;
    private int tries;
    private String[] directories = {"test", "examples", "debug", "samples"};

    private CodeOntology(String[] args) throws JSAPException {
        spoon = new Launcher();
        arguments = new CodeOntologyArguments(args);
        exploreJarsFlag = arguments.exploreJars() || (arguments.getJarInput() != null);
        ReflectionFactory.getInstance().setParent(spoon.createFactory());
        RDFLogger.getInstance().setOutputFile(arguments.getOutput());
        downloadDependencies = arguments.downloadDependencies();

        formatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSuffix(" hours, ")
                .appendMinutes()
                .appendSuffix(" minutes, ")
                .appendSeconds()
                .appendSuffix(" seconds, ")
                .appendMillis()
                .appendSuffix(" millis")
                .toFormatter();
    }

    public static void main(String[] args) {
        try {
            launcher = new CodeOntology(args);
            if (launcher.isInputSet()) {
                System.out.println("Running on " + launcher.getArguments().getInput());
                launcher.loadDependencies();
                if (!launcher.getArguments().doNotExtractTriples()) {
                    launcher.spoon();
                    launcher.extractAllTriples();
                }
            }
        } catch (Exception e) {
            handleFailure(e);
        }

        try {
            launcher.processJars();
            launcher.postCompletionTasks();
        } catch (Exception e) {
            handleFailure(e);
        }
    }

    private static void handleFailure(Exception e) {
        System.out.println("It was a good plan that went awry.");
        if (e.getMessage() != null) {
            System.out.println(e.getMessage());
        }
        if (launcher.getArguments().stackTraceMode()) {
            e.printStackTrace();
        }
        System.exit(-1);
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
        LoaderFactory factory = LoaderFactory.getInstance();
        loader = factory.getLoader(getArguments().getInput());
        loader.loadDependencies();

        String classpath = getArguments().getClasspath();

        if (classpath != null) {
            loader.loadClasspath(classpath);
        }
        long end = System.currentTimeMillis();
        System.out.println("Dependencies downloaded in " + formatter.print(new Period(start, end)) + ".");
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

    private void processJars() throws IOException {
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
        if (getLauncher().getArguments().shutdownFlag()) {
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

    public static CodeOntology getLauncher() {
        return launcher;
    }

    public CodeOntologyArguments getArguments() {
        return arguments;
    }

    public static boolean downloadDependencies() {
        return getLauncher().downloadDependencies;
    }

    public static void signalDependenciesDownloaded() {
        getLauncher().downloadDependencies = true;
    }

    public static boolean verboseMode() {
        return getLauncher().getArguments().verboseMode();
    }

    public static boolean isJarExplorationEnabled() {
        return getLauncher().exploreJarsFlag;
    }

    private boolean isInputSet() {
        return getArguments().getInput() != null;
    }

    private boolean removeDirectoriesByName(String name) {
        try {
            Path[] tests = Files.walk(Paths.get(getArguments().getInput()))
                    .filter(path -> path.toFile().getName().equals(name) && path.toFile().isDirectory())
                    .toArray(Path[]::new);

            if (tests.length == 0) {
                return false;
            }

            for (Path testPath : tests) {
                System.out.println("Removing " + testPath.toFile().getAbsolutePath());
                FileUtils.deleteDirectory(testPath.toFile());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static void showWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

}
