package org.codeontology;

import com.martiansoftware.jsap.*;

import java.io.File;

public class CodeOntologyArguments {

    public static final String INPUT_LONG = "input";
    public static final char INPUT_SHORT = 'i';

    public static final String OUTPUT_LONG = "output";
    public static final char OUTPUT_SHORT = 'o';

    public static final String CLASSPATH_LONG = "classpath";

    public static final String ND = "nd";

    public static final String VERBOSE_LONG = "verbose";
    public static final char VERBOSE_SHORT = 'v';

    public static final String STACKTRACE_LONG = "stacktrace";
    public static final char STACKTRACE_SHORT = 't';

    public static final String HELP_LONG = "help";
    public static final char HELP_SHORT = 'h';

    public static final String SHUTDOWN_LONG = "shutdown";

    public static final String JAR_INPUT_LONG = "jar";

    public static final String EXPLORE_DEPENDENCIES_LONG = "explore-dependencies";

    public static final String DO_NOT_EXTRACT_LONG = "do-not-extract";

    public static final String CLEAN_LONG = "clean";

    private JSAP jsap;
    private JSAPResult result;


    public CodeOntologyArguments(String[] args) throws JSAPException {
        defineArgs();
        result = parseArgs(args);
    }

    private void defineArgs() throws JSAPException {
        this.jsap = new JSAP();
        FlaggedOption option;
        Switch flag;

        option = new FlaggedOption(INPUT_LONG);
        option.setShortFlag(INPUT_SHORT);
        option.setLongFlag(INPUT_LONG);
        option.setStringParser(JSAP.STRING_PARSER);
        option.setHelp("Path to source files.");
        jsap.registerParameter(option);

        option = new FlaggedOption(OUTPUT_LONG);
        option.setShortFlag(OUTPUT_SHORT);
        option.setLongFlag(OUTPUT_LONG);
        option.setStringParser(JSAP.STRING_PARSER);
        option.setRequired(false);
        option.setDefault(getDefaultOutput());
        option.setHelp("Output file name.");
        jsap.registerParameter(option);

        option = new FlaggedOption(JAR_INPUT_LONG);
        option.setLongFlag(JAR_INPUT_LONG);
        option.setStringParser(JSAP.STRING_PARSER);
        option.setRequired(false);
        option.setHelp("Path to a jar input file");
        jsap.registerParameter(option);

        option = new FlaggedOption(CLASSPATH_LONG);
        option.setLongFlag(CLASSPATH_LONG);
        option.setStringParser(JSAP.STRING_PARSER);
        option.setRequired(false);
        option.setHelp("Specifies a list of directories and JAR files separated by colons (:) to search for class files.");
        jsap.registerParameter(option);

        flag = new Switch(ND);
        flag.setLongFlag(ND);
        flag.setDefault("false");
        flag.setHelp("Do not download dependencies.");
        jsap.registerParameter(flag);

        flag = new Switch(VERBOSE_LONG);
        flag.setLongFlag(VERBOSE_LONG);
        flag.setShortFlag(VERBOSE_SHORT);
        flag.setDefault("false");
        flag.setHelp("Verbosely lists all files processed.");
        jsap.registerParameter(flag);

        flag = new Switch(STACKTRACE_LONG);
        flag.setLongFlag(STACKTRACE_LONG);
        flag.setShortFlag(STACKTRACE_SHORT);
        flag.setDefault("false");
        flag.setHelp("Prints stack trace for exceptions.");
        jsap.registerParameter(flag);

        flag = new Switch(HELP_LONG);
        flag.setLongFlag(HELP_LONG);
        flag.setShortFlag(HELP_SHORT);
        flag.setDefault("false");
        flag.setHelp("Prints this help message.");
        jsap.registerParameter(flag);

        flag = new Switch(EXPLORE_DEPENDENCIES_LONG);
        flag.setLongFlag(EXPLORE_DEPENDENCIES_LONG);
        flag.setDefault("false");
        flag.setHelp("Explore jar files in classpath");
        jsap.registerParameter(flag);

        flag = new Switch(SHUTDOWN_LONG);
        flag.setLongFlag(SHUTDOWN_LONG);
        flag.setDefault("false");
        flag.setHelp("Shutdown after complete");
        jsap.registerParameter(flag);

        flag = new Switch(DO_NOT_EXTRACT_LONG);
        flag.setLongFlag(DO_NOT_EXTRACT_LONG);
        flag.setDefault("false");
        flag.setHelp("Do not extract triples, just download dependencies");
        jsap.registerParameter(flag);

        flag = new Switch(CLEAN_LONG);
        flag.setLongFlag(CLEAN_LONG);
        flag.setDefault("false");
        flag.setHelp("Remove tests if compilation fails.");
        jsap.registerParameter(flag);

    }

    public JSAPResult parseArgs(String[] args) throws JSAPException {
        defineArgs();

        JSAPResult arguments = jsap.parse(args);

        if (arguments.getBoolean(HELP_LONG)) {
            printHelp();
            System.exit(0);
        }

        if (!arguments.success()) {
            // print out specific error messages describing the problems
            java.util.Iterator<?> errs = arguments.getErrorMessageIterator();
            while (errs.hasNext()) {
                System.err.println("Error: " + errs.next());
            }

            printHelp();
            System.exit(-1);
        }


        return arguments;
    }

    private void printHelp() {
        printUsage();
        System.err.println("Options:");
        System.err.println();
        System.err.println(jsap.getHelp());
    }

    private void printUsage() {
        System.out.println("Usage:");
        System.out.println("codeontology -i <input_folder> -o <output_file>");
        System.out.println();
    }

    public String getInput() {
        return result.getString(INPUT_LONG);
    }

    public String getOutput() {
        return result.getString(OUTPUT_LONG);
    }

    public boolean downloadDependencies() {
        return !result.getBoolean(ND);
    }

    public boolean verboseMode() {
        return result.getBoolean(VERBOSE_LONG);
    }

    public boolean stackTraceMode() {
        return result.getBoolean(STACKTRACE_LONG);
    }

    public boolean shutdownFlag() {
        return result.getBoolean(SHUTDOWN_LONG);
    }

    public boolean doNotExtractTriples() {
        return result.getBoolean(DO_NOT_EXTRACT_LONG);
    }

    private String getDefaultOutput() {
        String extension = ".nt";
        String base = "triples";
        final int LIMIT = 100;

        String defaultName = base + extension;
        File file = new File(defaultName);
        int i = 1;
        while (i < LIMIT && file.exists()) {
            i++;
            defaultName = base + i + extension;
            file = new File(defaultName);
        }

        if (i == LIMIT) {
            throw new RuntimeException("Specify an output file");
        }

        return defaultName;
    }

    public String getJarInput() {
        return result.getString(JAR_INPUT_LONG);
    }

    public boolean exploreJars() {
        return result.getBoolean(EXPLORE_DEPENDENCIES_LONG);
    }

    public String getClasspath() {
        return result.getString(CLASSPATH_LONG);
    }

    public boolean removeTests() {
        return result.getBoolean(CLEAN_LONG);
    }
}