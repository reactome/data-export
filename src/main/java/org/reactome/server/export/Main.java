package org.reactome.server.export;

import com.martiansoftware.jsap.*;
import org.reactome.server.export.config.ReactomeNeo4jConfig;
import org.reactome.server.export.mapping.Mapping;
import org.reactome.server.export.opentargets.OpenTargetsExporter;
import org.reactome.server.export.tasks.common.DataExport;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.reflections.Reflections;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @author Florian Korninger <florian.korninger@ebi.ac.uk>
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("ALL")
@Configuration
public class Main {

    public static void main(String[] args) throws JSAPException {

        SimpleJSAP jsap = new SimpleJSAP(Main.class.getName(), "A tool for creating the files for the download section from the existing graph database",
                new Parameter[]{
                        new FlaggedOption(  "host",     JSAP.STRING_PARSER,  "localhost",     JSAP.REQUIRED,     'h', "host",     "The neo4j host"          ),
                        new FlaggedOption(  "port",     JSAP.STRING_PARSER,  "7474",          JSAP.NOT_REQUIRED, 'b', "port",     "The neo4j port"          ),
                        new FlaggedOption(  "user",     JSAP.STRING_PARSER,  "neo4j",         JSAP.REQUIRED,     'u', "user",     "The neo4j user"          ),
                        new FlaggedOption(  "password", JSAP.STRING_PARSER,  "neo4j",         JSAP.REQUIRED,     'p', "password", "The neo4j password"      ),
                        new FlaggedOption(  "output",   JSAP.STRING_PARSER,  null,            JSAP.REQUIRED,     'o', "output",   "Output folder"           ),
                        new FlaggedOption(  "task",     JSAP.STRING_PARSER,  null,            JSAP.NOT_REQUIRED, 't', "task",     "A specific task"         ),
                        new QualifiedSwitch("verbose",  JSAP.BOOLEAN_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'v', "verbose",  "Requests verbose output" )
                }
        );
        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        //Initialising ReactomeCore Neo4j configuration
        ReactomeGraphCore.initialise(config.getString("host"), config.getString("port"), config.getString("user"), config.getString("password"), ReactomeNeo4jConfig.class);

        GeneralService generalService = ReactomeGraphCore.getService(GeneralService.class);

        String task = config.getString("task");
        Reflections reflections = new Reflections("org.reactome.server.export.tasks");
        Set<Class<?>> tests = reflections.getTypesAnnotatedWith(org.reactome.server.export.annotations.DataExport.class);

        String path = config.getString("output");
        if (!path.endsWith("/")) path += "/";

        boolean verbose = config.getBoolean("verbose");

        /*############ IMPORTANT ############
        The export is divided in three sections:
            1) Generic Mapping Files -> A set of files per resource identifier pointing to different levels of Events
            2) On demand exports -> Based on users *interensting* requests, different files are generated
        ###################################*/

        //Only run the mapping if a specific task has not been specified
        if (task == null) Mapping.run(generalService, path, verbose);

        int n = tests.size(), i = 1, count = 0;
        for (Class test : tests) {
            try {
                Object object = test.newInstance();
                DataExport dataExport = (DataExport) object;
                if (task == null || dataExport.getName().equals(task)) {
                    if (verbose) {
                        if (task == null) System.out.print("\rRunning task " + dataExport.getName() + " [" + (i++) + " of " + n + "]");
                        else System.out.println("Running task " + dataExport.getName());
                    }
                    if (dataExport.run(generalService, path)) count++;
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (verbose && task == null) {
            System.out.println("\rTasks finished. " + count + " files has been generated.\n\nPlease ensure the files are available for download.");
        }

        if (task.equals("OpenTargetsExporter")) {
            OpenTargetsExporter.export(path, verbose);
        }
    }
}
