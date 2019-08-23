package org.reactome.server.export.tasks;

import org.neo4j.ogm.model.Result;
import org.reactome.server.export.annotations.DataExport;
import org.reactome.server.export.tasks.common.DataExportAbstract;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DataExport
public class ProteinRoleReaction extends DataExportAbstract {


    @Override
    public String getName() {
        return "ProteinRoleReaction";
    }

    @Override
    public String getQuery() {
        return "MATCH (rd:ReferenceDatabase)<--(n)<-[:referenceEntity|referenceSequence|crossReference|referenceGene*]-(pe:PhysicalEntity) "+
                "WHERE rd.displayName =~ \"UniProt\" "+
                "WITH n, COLLECT(DISTINCT pe) AS pes "+
                "UNWIND pes AS pe "+
                "MATCH (pe)<-[:physicalEntity|regulator|diseaseEntity|hasComponent|hasMember|hasCandidate|repeatedUnit*]-()<-[r:input|output|catalystActivity|regulatedBy|entityFunctionalStatus]-(rle:ReactionLikeEvent) "+
                "RETURN DISTINCT n.identifier AS UniProt, TYPE(r) AS Role, rle.stId AS Reaction "+
                "ORDER BY UniProt, Reaction";
    }

    @Override
    public void printResult(Result result, Path path) throws IOException {
        print(result, path, false, "UniProt", "Role", "Reaction");
    }

}
