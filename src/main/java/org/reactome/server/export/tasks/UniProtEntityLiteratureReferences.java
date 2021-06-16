package org.reactome.server.export.tasks;

import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * Requested by: DelawareBiotechnologyInstitute
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DataExport
public class UniProtEntityLiteratureReferences extends DataExportAbstract {
    @Override
    public String getName() {
        return "UniProt_Entity_LiteratureReferences";
    }

    @Override
    public String getQuery() {
        return "MATCH (rd:ReferenceDatabase)<--(n)<-[:referenceEntity|referenceSequence|crossReference|referenceGene*]-(pe:PhysicalEntity{speciesName:\"Homo sapiens\"}), " +
                "      (pe)<-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator|repeatedUnit|hasMember|hasCandidate|hasComponent|inferredTo*]-(rle:ReactionLikeEvent) " +
                "WHERE rd.displayName =~ \"UniProt\" " +
                "WITH n, pe, COLLECT(DISTINCT rle) AS rles " +
                "UNWIND rles AS rle " +
                "MATCH (rle)-[:literatureReference]->(lr:LiteratureReference) " +
                "RETURN DISTINCT n.identifier AS Identifier, " +
                "                pe.stId AS Entity, " +
                "                pe.displayName AS Entity_Name, " +
                "                COLLECT(DISTINCT lr.pubMedIdentifier) AS PMIDs " +
                "ORDER BY Identifier, Entity";
    }

    @Override
    public void printResult(Collection<Map<String, Object>> result, Path path) throws IOException {
        print(result, path, "Identifier", "Entity", "Entity_Name", "PMIDs");
    }
}
