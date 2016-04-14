package org.codeontology;

import com.martiansoftware.jsap.*;

import java.io.File;

public class CodeOntologyArguments {

    public static final String INPUT_LONG_FLAG = "input";
    public static final char INPUT_SHORT_FLAG = 'i';

    public static final String OUTPUT_LONG_FLAG = "output";
    public static final char OUTPUT_SHORT_FLAG = 'o';

    public static final String CLASSPATH_LONG_FLAG = "classpath";

    public static final String ND = "nd";

    public static final String VERBOSE_LONG_FLAG = "verbose";
    public static final char VERBOSE_SHORT_FLAG = 'v';

    public static final String STACKTRACE_LONG_FLAG = "stacktrace";
    public static final char STACKTRACE_SHORT_FLAG = 't';

    public static final String HELP_LONG_FLAG = "help";
    public static final char HELP_SHORT_FLAG = 'h';

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

        option = new FlaggedOption(INPUT_LONG_FLAG);
        option.setShortFlag(INPUT_SHORT_FLAG);
        option.setLongFlag(INPUT_LONG_FLAG);
        option.setStringParser(JSAP.STRING_PARSER);
        option.setRequired(true);
        option.setHelp("Path to source files.");
        jsap.registerParameter(option);

        option = new FlaggedOption(OUTPUT_LONG_FLAG);
        option.setShortFlag(OUTPUT_SHORT_FLAG);
        option.setLongFlag(OUTPUT_LONG_FLAG);
        option.setStringParser(JSAP.STRING_PARSER);
        option.setRequired(false);
        option.setDefault(getDefaultOutput());
        option.setHelp("Output file name.");
        jsap.registerParameter(option);

        option = new FlaggedOption(CLASSPATH_LONG_FLAG);
        option.setLongFlag(CLASSPATH_LONG_FLAG);
        option.setStringParser(JSAP.STRING_PARSER);
        option.setRequired(false);
        option.setHelp("Specifies a list of directories, JAR files and classes separated by colons (:) to search for class files.");
        jsap.registerParameter(option);

        flag = new Switch(ND);
        flag.setLongFlag(ND);
        flag.setDefault("false");
        flag.setHelp("Do not download dependencies.");
        jsap.registerParameter(flag);

        flag = new Switch(VERBOSE_LONG_FLAG);
        flag.setLongFlag(VERBOSE_LONG_FLAG);
        flag.setShortFlag(VERBOSE_SHORT_FLAG);
        flag.setDefault("false");
        flag.setHelp("Verbosely lists all files processed.");
        jsap.registerParameter(flag);

        flag = new Switch(STACKTRACE_LONG_FLAG);
        flag.setLongFlag(STACKTRACE_LONG_FLAG);
        flag.setShortFlag(STACKTRACE_SHORT_FLAG);
        flag.setDefault("false");
        flag.setHelp("Prints stack trace for exceptions.");
        jsap.registerParameter(flag);

        flag = new Switch(HELP_LONG_FLAG);
        flag.setLongFlag(HELP_LONG_FLAG);
        flag.setShortFlag(HELP_SHORT_FLAG);
        flag.setDefault("false");
        flag.setHelp("Prints this help message.");
        jsap.registerParameter(flag);

    }

    public JSAPResult parseArgs(String[] args) throws JSAPException {
        defineArgs();

        JSAPResult arguments = jsap.parse(args);

        if (arguments.getBoolean(HELP_LONG_FLAG)) {
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
        return result.getString(INPUT_LONG_FLAG);
    }

    public String getOutput() {
        return result.getString(OUTPUT_LONG_FLAG);
    }

    public boolean getDownloadDependenciesFlag() {
        return !result.getBoolean(ND);
    }

    public boolean getVerboseMode() {
        return result.getBoolean(VERBOSE_LONG_FLAG);
    }

    public boolean stackTraceMode() {
        return result.getBoolean(STACKTRACE_LONG_FLAG);
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

    public String getClasspath() {
        return result.getString(CLASSPATH_LONG_FLAG);
    }
}