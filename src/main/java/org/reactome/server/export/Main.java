package org.reactome.server.export;

import com.martiansoftware.jsap.*;
import org.reactome.server.export.common.DataExport;
import org.reactome.server.export.common.DataExportAbstract;
import org.reactome.server.export.config.GraphQANeo4jConfig;
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

        SimpleJSAP jsap = new SimpleJSAP(Main.class.getName(), "A tool for testing the integrity and consistency of the data imported in the existing graph database",
                new Parameter[]{
                        new FlaggedOption(  "host",     JSAP.STRING_PARSER, "localhost",     JSAP.REQUIRED,     'h', "host",     "The neo4j host"),
                        new FlaggedOption(  "port",     JSAP.STRING_PARSER, "7474",          JSAP.NOT_REQUIRED, 'b', "port",     "The neo4j port"),
                        new FlaggedOption(  "user",     JSAP.STRING_PARSER, "neo4j",         JSAP.REQUIRED,     'u', "user",     "The neo4j user"),
                        new FlaggedOption(  "password", JSAP.STRING_PARSER, "reactome",      JSAP.REQUIRED,     'p', "password", "The neo4j password"),
                        new FlaggedOption(  "output",   JSAP.STRING_PARSER, "./export/",      JSAP.REQUIRED,     'o', "output",   "Output folder"),
                        new QualifiedSwitch("verbose",  JSAP.BOOLEAN_PARSER, JSAP.NO_DEFAULT,JSAP.NOT_REQUIRED, 'v', "verbose",  "Requests verbose output")
                }
        );
        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        //Initialising ReactomeCore Neo4j configuration
        ReactomeGraphCore.initialise(config.getString("host"), config.getString("port"), config.getString("user"), config.getString("password"), GraphQANeo4jConfig.class);

        GeneralService genericService = ReactomeGraphCore.getService(GeneralService.class);

        Reflections reflections = new Reflections("org.reactome.server.export.tasks");
        Set<Class<?>> tests = reflections.getTypesAnnotatedWith(org.reactome.server.export.annotations.DataExport.class);

        DataExportAbstract.setPath(config.getString("output"));
        boolean verbose = config.getBoolean("verbose");
        int n = tests.size(), i = 1, count = 0;
        for (Class test : tests) {
            try {
                Object object = test.newInstance();
                DataExport qATest = (DataExport) object;
                if(verbose) System.out.print("\rRunning task " + (i++) + " of " + n);
                if(qATest.run(genericService)) count++;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(verbose) System.out.println("\r" + count + " files has been generated.\nPlease ensure the files are available for download.");
    }
}
