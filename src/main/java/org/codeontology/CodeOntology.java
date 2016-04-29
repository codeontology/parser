package org.codeontology;

import com.martiansoftware.jsap.JSAPException;
import org.codeontology.buildsystems.DependenciesLoader;
import org.codeontology.buildsystems.LoaderFactory;
import org.codeontology.extraction.JarProcessor;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.SourceProcessor;
import spoon.Launcher;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class CodeOntology {
    private static CodeOntology launcher;
    private CodeOntologyArguments arguments;
    private Launcher spoon;
    private boolean exploreJarsFlag;
    private DependenciesLoader loader;

    private CodeOntology(String[] args) throws JSAPException {
        spoon = new Launcher();
        arguments = new CodeOntologyArguments(args);
        exploreJarsFlag = arguments.exploreJars() || (arguments.getJarInput() != null);
        ReflectionFactory.getInstance().setParent(spoon.createFactory());
        RDFLogger.getInstance().setOutputFile(arguments.getOutput());
    }

    public static void main(String[] args) {
        try {
            launcher = new CodeOntology(args);

            if (launcher.isInputSet()) {
                launcher.loadDependencies();
                if (!launcher.getArguments().doNotExtractTriples()) {
                    launcher.spoon();
                    launcher.extractAllTriples();
                }
            }

            launcher.processJars();
            launcher.postCompletionTasks();
        } catch (Exception e) {
            System.out.println("It was a good plan that went awry.");
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
            if (launcher.getArguments().stackTraceMode()) {
                e.printStackTrace();
            }
            System.exit(-1);
        }
    }

    private void spoon() {
        checkInput();
        spoon.addInputResource(getArguments().getInput());
        System.out.println("Building model...");
        spoon.buildModel();
        System.out.println("Model built successfully.");
    }

    private void loadDependencies() {
        LoaderFactory factory = LoaderFactory.getInstance();
        loader = factory.getLoader(getArguments().getInput());
        loader.loadDependencies();

        String classpath = getArguments().getClasspath();

        if (classpath != null) {
            loader.loadClasspath(classpath);
        }
    }

    private void extractAllTriples() {
        System.out.println("Extracting triples for " + getArguments().getInput() + "...");
        spoon.addProcessor(new SourceProcessor());
        spoon.process();
        RDFLogger.getInstance().writeRDF();
        System.out.println("Triples extracted successfully.");
    }

    private void processJars() throws IOException {
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
        }
    }

    private void postCompletionTasks() {
        if (getLauncher().getArguments().shutdownFlag()) {
            Thread shutdownThread = new Thread(() -> {
                try {
                    System.out.println("Shutting down...");
                    ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "sleep 3; shutdown -h now");
                    processBuilder.start();
                } catch (IOException e) {
                    System.out.println("Shutdown failed");
                }
            });
            Runtime.getRuntime().addShutdownHook(shutdownThread);
        }
    }

    private void checkInput() {
        File input = new File(getArguments().getInput());
        if (!input.exists()) {
            System.out.println("Folder " + input.getPath() + " doesn't seem to exist.");
            System.exit(-1);
        }
        if (!input.canRead() && !input.setReadable(true)) {
            System.out.println("Folder " + input.getPath() + " doesn't seem to be readable.");
            System.exit(-1);
        }
    }

    public static CodeOntology getLauncher() {
        return launcher;
    }

    public CodeOntologyArguments getArguments() {
        return arguments;
    }

    public static boolean getDownloadDependenciesFlag() {
        return getLauncher().getArguments().downloadDependencies();
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

}
