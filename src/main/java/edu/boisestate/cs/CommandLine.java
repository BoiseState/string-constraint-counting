package edu.boisestate.cs;

import org.apache.commons.cli.*;

import java.io.File;
import java.util.List;

/**
 *
 */
class CommandLine {

    static Settings processArgs(String[] args) {

        // create options
        Options options = createOptions();

        // create command line parser
        CommandLineParser parser = new DefaultParser();
        org.apache.commons.cli.CommandLine commandLine;

        try {

            // parse command line arguments
            commandLine = parser.parse(options, args);

        } catch (ParseException e) {

            // error parsing arguments, show error and return null
            System.err.format(
                    "Error processing command line arguments. Reason: %s",
                    e.getMessage());
            return null;
        }

        // create settings object
        Settings settings = new Settings();

        // process help option
        if (commandLine.hasOption("h")) {

            printHelp(options);

            // no other action needed, return null
            return null;
        }

        // ensure valid unprocessed arguments
        List<String> argsList = commandLine.getArgList();
        if (argsList.size() != 1 ||
            argsList.get(0) == null ||
            !isValidGraphFile(argsList.get(0))) {

            // error parsing arguments, show error and return null
            System.err.println(
                    "Invalid arguments have been specified, please consult " +
                    "usage documentation for help with the -h or --help " +
                    "option");

            return null;
        }

        // get graph file from unprocessed arguments list
        settings.setGraphFilePath(argsList.get(0));

        // process debug option
        if (commandLine.hasOption("d")) {
            settings.setDebug(true);
        }

        // process bounding length option
        if (commandLine.hasOption("l")) {

            // set initial bounding length from option value
            String optionValue = commandLine.getOptionValue("l");
            int boundingLength = Integer.parseInt(optionValue);
            settings.setInitialBoundingLength(boundingLength);
        }

        // return updated settings object
        return settings;
    }

    private static boolean isValidGraphFile(String filePath) {
        File graphFile = new File(filePath);
        return graphFile.exists() && (filePath.endsWith(".json") || filePath.endsWith(".smt2") || filePath.endsWith(".ser"));
    }

    private static void printHelp(Options options) {

        // create formatter
        HelpFormatter formatter = new HelpFormatter();

        formatter.setSyntaxPrefix("USAGE:\n\n" + padding(2));

        String appClass = "java " + SolveMain.class.getName();

        StringBuilder header = new StringBuilder();

        // description
        header.append("\nRun the inverse string constraint solver on the")
              .append(" specified constraint graph, generating concrete")
              .append(" satisfying input assignments.");

        // section header for options
        header.append("\n\nOPTIONS:\n\n");

        StringBuilder footer = new StringBuilder();

        // section header for example usage
        footer.append("\n\nUSAGE EXAMPLES:");

        // example
        footer.append("\n\n")
              .append(padding(4))
              .append(appClass)
              .append(" <PROJECT_ROOT>/graphs/iText02.json")
              .append("\n")
              .append(padding(8))
              .append("-l 10 -d");

        // example explanation
        footer.append("\n\nSolve the iText02.json constraint graph with an")
              .append(" initial bounding length of 10 and debug output.");

        // section header for additional information
        footer.append("\n\nADDITIONAL INFORMATION:");

        // additional information description
        footer.append("\n\nSee the code repository at https://github.com/")
              .append("BoiseState/string-constraint-counting for more ")
              .append("details.\n");

        formatter.printHelp(appClass + " <Graph File>",
                            header.toString(),
                            options,
                            footer.toString(),
                            true);
    }

    private static String padding(int length) {

        // create char array of length with blank spaces
        char[] pad = new char[length];
        for (int i = 0; i < length; i++) {
            pad[i] = ' ';
        }

        // return string created from pad array
        return new String(pad);
    }

    private static Options createOptions() {

        // debug mode flag
        Option debug = Option.builder("d")
                             .longOpt("debug")
                             .desc("Runs the solver framework in debug mode." +
                                   " Default value is false.")
                             .build();

        // help / usage option
        Option help = Option.builder("h")
                            .longOpt("help")
                            .desc("Display this message.")
                            .build();

        // automaton bounding length option
        Option length = Option.builder("l")
                              .longOpt("length")
                              .desc("Initial bounding length of the " +
                                    "underlying symbolic string. " +
                                    "Default value is " +
                                    Settings.DEFAULT_BOUNDING_LENGTH + ".")
                              .hasArg()
                              .numberOfArgs(1)
                              .argName("length")
                              .build();

        // add each option to options collection
        Options options = new Options();
        options.addOption(debug);
        options.addOption(help);
        options.addOption(length);

        // return options
        return options;
    }
}
