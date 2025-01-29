package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * Export Pathway Summation Mapping from Neo4j
 */
@DataExport
public class HumanPathwaysWithDiagrams extends DataExportAbstract {

    @Override
    public String getName() {
        return "humanPathwaysWithDiagrams";
    }

    @Override
    public String getQuery() {
        return "MATCH (p:Pathway)-[:species]->(:Species {dbId: 48887})\n"+
        "OPTIONAL MATCH (p)-[:representedPathway]->(diagram:PathwayDiagram)\n"+
        "OPTIONAL MATCH (p)-[:disease]->(d:Disease)\n"+
        "WITH p, diagram, d IS NOT NULL AS isDisease\n"+
        "WHERE diagram IS NOT NULL\n"+ 
        "AND NOT ALL((diagram)-[:hasNode]->(n) WHERE n:GreenBoxDiagram OR n:Compartment)\n"+
        "WITH p.dbId AS pathwayId, p.displayName AS pathwayName, isDisease\n"+
        "CALL apoc.export.csv.query(\n" +
        "\"RETURN $pathwayId AS PathwayID, $pathwayName AS PathwayName, $isDisease AS IsDisease\",\n"+  
        "'humanPathwaysWithDiagrams.csv',\n" + 
        "{headers: true}\n) YIELD file\n"+
        "RETURN 'File exported to: ' + file";
    }
}
