/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.api;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.logging.LogFactory;

import io.github.starmoon1617.starmie.generator.core.util.ConfigurationUtils;

/**
 * to run code generator
 * 
 * @date 2023-10-20
 * @author Nathan Liao
 */
public class ShellRunner {

    private static final String CONFIG_FILE = "-configfile";
    private static final String OVERWRITE = "-overwrite";
    private static final String CONTEXT_IDS = "-contextids";
    private static final String TABLES = "-tables";
    private static final String VERBOSE = "-verbose";
    private static final String FORCE_JAVA_LOGGING = "-forceJavaLogging";
    private static final String HELP_1 = "-?";
    private static final String HELP_2 = "-h";

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            System.exit(0);
            return; // only to satisfy compiler, never returns
        }

        Map<String, String> arguments = parseCommandLine(args);

        if (arguments.containsKey(HELP_1)) {
            usage();
            System.exit(0);
            return; // only to satisfy compiler, never returns
        }

        if (!arguments.containsKey(CONFIG_FILE)) {
            writeLine(getString("RuntimeError.0")); //$NON-NLS-1$
            return;
        }

        List<String> warnings = new ArrayList<>();

        String configfile = arguments.get(CONFIG_FILE);
        File configurationFile = new File(configfile);
        if (!configurationFile.exists()) {
            writeLine(getString("RuntimeError.1", configfile)); //$NON-NLS-1$
            return;
        }

        Set<String> fullyQualifiedTables = StringUtility.tokenize(arguments.get(TABLES));

        Set<String> contexts = StringUtility.tokenize(arguments.get(CONTEXT_IDS));

        try {
            Properties properties = new Properties();
            properties.load(new FileReader(configurationFile));
            Configuration config = ConfigurationUtils.createConfiguration(properties);

            DefaultShellCallback shellCallback = new DefaultShellCallback(arguments.containsKey(OVERWRITE));

            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);

            ProgressCallback progressCallback = arguments.containsKey(VERBOSE) ? new VerboseProgressCallback() : null;

            myBatisGenerator.generate(progressCallback, contexts, fullyQualifiedTables);

        } catch (SQLException | IOException e) {
            e.printStackTrace(System.out);
            return;
        } catch (InvalidConfigurationException e) {
            writeLine(getString("Progress.16")); //$NON-NLS-1$
            for (String error : e.getErrors()) {
                writeLine(error);
            }
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (String warning : warnings) {
            writeLine(warning);
        }

        if (warnings.isEmpty()) {
            writeLine(getString("Progress.4")); //$NON-NLS-1$
        } else {
            writeLine();
            writeLine(getString("Progress.5")); //$NON-NLS-1$
        }
    }

    private static void usage() {
        writeLine(getString("Usage")); //$NON-NLS-1$
    }

    /**
     * write the message
     * 
     * @param message
     */
    private static void writeLine(String message) {
        System.out.println(message);
    }

    /**
     * next line
     */
    private static void writeLine() {
        System.out.println();
    }

    /**
     * parse the command
     * 
     * @param args
     * @return
     */
    private static Map<String, String> parseCommandLine(String[] args) {
        List<String> errors = new ArrayList<>();
        Map<String, String> arguments = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (CONFIG_FILE.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(CONFIG_FILE, args[i + 1]);
                } else {
                    errors.add(getString("RuntimeError.19", CONFIG_FILE)); //$NON-NLS-1$
                }
                i++;
            } else if (OVERWRITE.equalsIgnoreCase(args[i])) {
                arguments.put(OVERWRITE, "Y"); //$NON-NLS-1$
            } else if (VERBOSE.equalsIgnoreCase(args[i])) {
                arguments.put(VERBOSE, "Y"); //$NON-NLS-1$
            } else if (HELP_1.equalsIgnoreCase(args[i])) {
                arguments.put(HELP_1, "Y"); //$NON-NLS-1$
            } else if (HELP_2.equalsIgnoreCase(args[i])) {
                // put HELP_1 in the map here too - so we only
                // have to check for one entry in the mainline
                arguments.put(HELP_1, "Y"); //$NON-NLS-1$
            } else if (FORCE_JAVA_LOGGING.equalsIgnoreCase(args[i])) {
                LogFactory.forceJavaLogging();
            } else if (CONTEXT_IDS.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(CONTEXT_IDS, args[i + 1]);
                } else {
                    errors.add(getString("RuntimeError.19", CONTEXT_IDS)); //$NON-NLS-1$
                }
                i++;
            } else if (TABLES.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(TABLES, args[i + 1]);
                } else {
                    errors.add(getString("RuntimeError.19", TABLES)); //$NON-NLS-1$
                }
                i++;
            } else {
                errors.add(getString("RuntimeError.20", args[i])); //$NON-NLS-1$
            }
        }

        if (!errors.isEmpty()) {
            for (String error : errors) {
                writeLine(error);
            }

            System.exit(-1);
        }

        return arguments;
    }

}
