package org.codeontology.frontend;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.codeontology.frontend.StandardFrontEnd.StandardFrontEnd;
import org.codeontology.frontend.builds.BuildTool;
import org.codeontology.frontend.gradle.GradleFrontEnd;
import org.codeontology.frontend.maven.MavenFrontEnd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * Frontend for the application.
 */
public class JPPFrontEnd {

    private static Stack<File> inputFolders = new Stack<>();
    private static Stack<File> outputFiles = new Stack<>();
    /** Standard classpath to use when none are given. */
    private static File standardClasspath;
    private static String classpathString = "";


    public static void main(String[] args) throws Exception {
        System.out.println("Running...");
        run(args);
        System.out.println("Done.");
    }

    public static void run(String[] args) throws Exception {

        if (args.length < 3 || args.length %2 == 0) {
            showHelp();
            System.exit(-1);
        }

        for (int i = 0; i < args.length - 1; i++) {
            System.out.println("arg " + i + ": " + args[i]);
            File current = new File(args[i]);

            if (i % 2 == 1) {
                if (!current.exists()) {
                    showNotExistingError(current);
                    throw new IllegalArgumentException();
                }
                if (!current.canRead() && !current.setReadable(true)) {
                    showCantReadError(current);
                    throw new IllegalArgumentException();
                }
                if (!current.isDirectory()) {
                    showNotADirectoryError(current);
                    throw new IllegalArgumentException();
                }

                inputFolders.push(current);
            } else {
                outputFiles.push(new File(args[i]));
            }
        }

        for (int i = 0; i < inputFolders.size(); i++) {
            File currentInput = inputFolders.pop();
            File currentOutput = outputFiles.pop();

            BuildTool build = BuildTool.is(currentInput);
            System.out.println("Recognized as " + build.name() + " project");

            try {
                boolean downloadDependencies = args[args.length - 1].equals("d");
                System.out.println("Download: " + downloadDependencies);

                switch (build) {
                    case MAVEN:
                        MavenFrontEnd.getInstance().run(currentInput, currentOutput, downloadDependencies);
                        break;
                    case GRADLE:
                        GradleFrontEnd.getInstance().run(currentInput, currentOutput, downloadDependencies);
                    case ANT:
                    case UNKNOWN:
                        StandardFrontEnd.getInstance().run(currentInput, standardClasspath, currentOutput, false);
                }
            } catch (Exception e) {
                System.out.println("Error, deleting output file");
                e.printStackTrace();
                currentOutput.delete();
                e.printStackTrace();

            }
        }

    }

    private static void showNotADirectoryError (File path) {
        System.out.println(path.getPath() + " is not a folder");
    }


    private static void showHelp() {
        System.out.println("Usage: ");
        System.out.println("python jpp.py \"path/to/source output_file\"{n} (d|nd) (classpath)*");
    }


    private static void showNotExistingError(File path) {
        System.out.println("Folder " + path.getPath() + " doesn't seem to exist.");
    }

    private static void showCantReadError(File path) {
        System.out.println("Folder " + path.getPath() + " doesn't seem to be readable.");
    }


    public void setStandardClasspath(File standardClasspath) {
        JPPFrontEnd.standardClasspath = standardClasspath;
    }


    public File getStandardClasspath() {
        return standardClasspath;
    }


    /**
     * Run the script
     * @param root          The project root.
     * @param outputFile    The output file.
     * @param classpath     Optional classpaths.
     */
    protected void runScript (File root, File outputFile, boolean getJars, File... classpath) throws IOException,
                                                                                                        InterruptedException {

        try {
            Set<File> classpaths = new HashSet<>();
            Set<File> jars = new HashSet<>();
            String classpathString = "";
            Set<File> localJars = new HashSet<>();
            Set<File> remoteJars = new HashSet<>();

            Collections.addAll(classpaths, classpath);
            for (File path : classpaths)
                jars.addAll(findJars(path));

            jars.forEach(jar -> {
                if (jar.getPath().contains("/.m2/repository/"))
                    remoteJars.add(jar);
                else
                    localJars.add(jar);
            });


            if (getJars) {
                if (localJars.size() > 0) {
                    localJars.forEach(localJar -> {
                        try {
                            //FileUtils.copyFile(localJar, new File(root.getPath() + "/" + localJar.getName()));
                            FileUtils.copyFile(localJar, new File(System.getProperty("user.home") + "/.m2/repository/" + localJar.getName()));
                        } catch (IOException e1) {
                            //e1.printStackTrace();
                        }
                    });

                }
                if (remoteJars.size() > 0) {
                    remoteJars.forEach(remoteJar -> {
                        try {
                            //FileUtils.copyFile(remoteJar, new File(root.getPath() + "/" + remoteJar.getName()));
                            FileUtils.copyFile(remoteJar, new File(System.getProperty("user.home") + "/.m2/repository/" + remoteJar.getName()));
                        } catch (IOException e1) {
                            //e1.printStackTrace();
                        }
                    });
                }

            }

            classpathString = classpathString.concat(System.getProperty("user.home") + "/.m2/repository/*:");
            classpathString = classpathString.concat(root.getPath() + "/*:");
            System.out.println("Classpath created: " + classpathString);
            List<String> cmd = new ArrayList<>();
            cmd.add("bash");
            cmd.add("run");
            cmd.add(root.getPath());
            cmd.add(outputFile.getName());

            try {
                System.out.println("Running on " + root.getPath());
                if (classpathString.length() > 0) {
                    cmd.add(classpathString);
                }
                ProcessBuilder processBuilder = new ProcessBuilder(cmd.toArray(new String[cmd.size()]));
                processBuilder.directory(new File(System.getProperty("user.dir") + "/"));
                processBuilder.redirectError(new File(System.getProperty("user.dir") + "/ error"));
                processBuilder.redirectOutput(new File(System.getProperty("user.dir") + "/output"));
                Process process = processBuilder.start();
                process.waitFor();

            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Error occurred");
                e.printStackTrace();
                (new FileWriter(new File(System.getProperty("user.dir") + "/exec"))).append('\n' + "/usr/bin/bash run " + root.getPath() + " "
                         + outputFile + " "
                         + classpathString);
                throw e;
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }

    }


    /**
     * Find jars in given paths.
     * @param roots  The roots.
     * @return       A set with contained jars.
     */
    private Set<File> findJars(File... roots) {
        Set<File> jars = new HashSet<>();

        for (File root : roots)
            jars.addAll(FileUtils.listFiles(root,
                                            FileFilterUtils.suffixFileFilter(".jar"),
                                            TrueFileFilter.INSTANCE));

        return jars;
    }


    /**
     * Get the short path of {@code path}.
     * @return      The shortened path.
     */
    private String shortPath(File base) {
        try {
            System.out.println("Shortpath for " + base.getAbsolutePath());
            Set<File> jars = findJars(base);
            Set<Integer> depths = new HashSet<>();
            String path = "";

            jars.forEach(jar -> {
                System.out.println("path: " + jar.getAbsolutePath());
                depths.add(StringUtils.countMatches(jar.getAbsolutePath(), "/") - 5);
                if (!depths.contains(StringUtils.countMatches(jar.getAbsolutePath(), "/") - 5))
                    System.out.println("Adding " + (StringUtils.countMatches(jar.getAbsolutePath(), "/") - 5));
            });
            for (int count : depths) {
                String support = "";

                for (int i = 0; i < count; i++)
                    support = support + "/*";

                path = path.concat(base + support + "/*.jar:");
                System.out.println("Path " + path);
            }

            if (path.endsWith(":"))
                return path.substring(0, path.lastIndexOf(":") - 1);

            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
