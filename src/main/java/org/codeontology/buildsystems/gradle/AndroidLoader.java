package org.codeontology.buildsystems.gradle;

import org.apache.commons.io.FileUtils;
import org.apache.jena.web.HttpSC;
import org.codeontology.CodeOntology;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidLoader extends GradleLoader {

    public AndroidLoader(File root) {
        super(root);
    }

    @Override
    public void loadDependencies() {
        System.out.println("Loading dependencies for Android project...");
        addClasspathTask();
        removeLocalProperties();
        build();
        runTask("CodeOntologyCpFile");
        CodeOntology.signalDependenciesDownloaded();
        loadClasspath();
        loadAndroidSdkDependencies();
    }

    private void removeLocalProperties() {
        File localProperties = new File(getRoot().getPath() + "/../local.properties");
        if (localProperties.exists()) {
            try {
                FileUtils.forceDelete(localProperties);
            } catch (IOException e) {
                CodeOntology.showWarning("Could not delete local properties");
            }
        }
    }

    private void build() {
        try {
            String gradlew = "../gradlew";
            String chmod = "chmod +x " + gradlew;
            String build = gradlew + " build";
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", chmod + "; " + build);
            builder.directory(getRoot());
            builder.redirectError(error);
            builder.redirectOutput(output);
            builder.start().waitFor();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addClasspathTask() {
        String name = "CodeOntologyCpFile";
        String body =  "{\n" +
                "\tbuildDir.mkdirs()\n" +
                "\tandroid.applicationVariants.all { variant -> \n" +
                "\t\tnew File(buildDir, \"cp\").text = variant.javaCompile.classpath.asPath\t\n" +
                "\t}\n" +
                "}";
        addTask(name, body);
    }

    private void loadAndroidSdkDependencies() {
        String androidHome = System.getenv().get("ANDROID_HOME");
        if (androidHome == null) {
            CodeOntology.showWarning("ANDROID_HOME environment variable is not set.");
        }
        File appBuild = new File(getRoot().getPath() + "/build.gradle");

        String build = "";

        try (Scanner scanner = new Scanner(appBuild)) {
            scanner.useDelimiter("\\Z");
            if (scanner.hasNext()) {
                build = scanner.next();
            }
        } catch (FileNotFoundException e) {
            CodeOntology.showWarning("Could not find file build.gradle for module app.");
        }

        String sdkVersion = null;

        String androidBlock = ".*android\\s*\\{.*";
        Pattern pattern = Pattern.compile(androidBlock + "compileSdkVersion\\s+([0-9]+).*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(build);
        if (matcher.matches()) {
            sdkVersion = matcher.group(1);
        } else {
            pattern = Pattern.compile(androidBlock + "targetSdkVersion\\s+([0-9]+).*", Pattern.DOTALL);
            matcher = pattern.matcher(build);
            if (matcher.matches()) {
                sdkVersion = matcher.group(1);
            }
        }
        if (sdkVersion != null) {
            getLoader().loadAllJars(androidHome + "/platforms/android-" + sdkVersion);
        } else {
            CodeOntology.showWarning("Could not find sdk version.");
        }
    }
}
