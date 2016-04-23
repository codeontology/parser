package org.codeontology;

import com.martiansoftware.jsap.JSAPException;
import org.codeontology.buildsystems.DependenciesLoader;
import org.codeontology.buildsystems.LoaderFactory;
import org.codeontology.extraction.RDFLogger;
import org.codeontology.extraction.SourceProcessor;
import spoon.Launcher;

import java.io.File;
import java.io.IOException;

public class CodeOntology {
    private static CodeOntology launcher;
    private CodeOntologyArguments arguments;
    private Launcher spoon;

    private CodeOntology(String[] args) throws JSAPException {
        spoon = new Launcher();
        arguments = new CodeOntologyArguments(args);
    }

    public static void main(String[] args) {
        try {
            launcher = new CodeOntology(args);
            launcher.loadDependencies();
            launcher.spoon();
            launcher.extractAllTriples();
            launcher.postCompletionTasks();
        } catch (Exception e) {
            System.out.println("Sorry, something went awry.");
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
        DependenciesLoader loader = factory.getLoader(getArguments().getInput());
        loader.loadDependencies();

        String classpath = getArguments().getClasspath();

        if (classpath != null) {
            loader.loadClasspath(classpath);
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

    private void extractAllTriples() {
        RDFLogger logger = RDFLogger.getInstance();
        logger.setOutputFile(getArguments().getOutput());
        System.out.println("Extracting triples...");
        spoon.addProcessor(new SourceProcessor());
        spoon.process();
        logger.writeRDF();
        System.out.println("Triples extracted successfully.");
    }

    public static CodeOntology getLauncher() {
        return launcher;
    }

    public CodeOntologyArguments getArguments() {
        return arguments;
    }

    public static boolean getDownloadDependenciesFlag() {
        return getLauncher().getArguments().getDownloadDependenciesFlag();
    }

    public static boolean verboseMode() {
        return getLauncher().getArguments().getVerboseMode();
    }

    private void postCompletionTasks() {
        if (getLauncher().getArguments().getShutdownFlag()) {
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
}
