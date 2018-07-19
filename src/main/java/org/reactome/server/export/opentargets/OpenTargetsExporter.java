package org.reactome.server.export.opentargets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactome.server.export.opentargets.model.EvidenceString;
import org.reactome.server.export.opentargets.query.ReactomeEvidence;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("WeakerAccess")
public class OpenTargetsExporter {

    static final String FILE_NAME = "OT_target_disease_association_v";

    static final String FILE_EXTENSION = ".json";

    public static Integer REACTOME_VERSION;

    private static final String query = "" +
            "MATCH (d:Disease)<-[:disease]-(rle:ReactionLikeEvent)-[:entityFunctionalStatus]->(efs:EntityFunctionalStatus), " +
            "      (p:Pathway)-[:hasEvent]->(rle), " +
            "      (efs)-[:functionalStatus|functionalStatusType*]->(fst:FunctionalStatusType), " +
            "      (efs)-[:physicalEntity|hasComponent|hasMember|hasCandidate|repeatedUnit*]->(pe:PhysicalEntity)-[:species]->(:Species{displayName:\"Homo sapiens\"}), " +
            "      (pe)-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"}) " +
            //pe is meant to differentiate the mutations per reference entity
            "WITH DISTINCT rle, pe, re, d, fst, COLLECT(DISTINCT {stId: p.stId, displayName: p.displayName}) AS pathways " +
            "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(gmr:GeneticallyModifiedResidue) " +
            "OPTIONAL MATCH (rle)-[:literatureReference]->(lr:LiteratureReference) " +
            "OPTIONAL MATCH (rle)-[:created]->(c:InstanceEdit) " +
            //pe is meant to differentiate the mutations per reference entity -> DO NOT DELETE even though isn't used below
            "WITH DISTINCT rle, c, pe, re, COLLECT(DISTINCT gmr.displayName) AS mutations, d, fst, pathways, COLLECT(DISTINCT lr.pubMedIdentifier) AS pubMedIdentifiers " +
            "RETURN DISTINCT rle.stId AS reaction, " +
            "       CASE WHEN rle.releaseDate IS NOT NULL THEN rle.releaseDate ELSE c.dateTime END AS releaseDate, " +
            "       re.databaseName AS resource, " +
//            "       CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END AS identifier, " +
            "       re.identifier AS identifier, " +
            "       mutations, " +
            "       d.identifier AS doid, " +
            "       d.databaseName AS diseaseResource, " +
            "       d.displayName AS disease, " +
            "       fst.displayName AS activity, " +
            "       pathways,  " +
            "       pubMedIdentifiers " +
            "ORDER BY rle.stId " +
            "UNION " +
            "MATCH (d:Disease)<-[:disease]-(rle:ReactionLikeEvent)<-[:hasEvent]-(p:Pathway), " +
            "      (rle)-[:input|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit*]->(pe:PhysicalEntity)-[:species]->(:Species{displayName:\"Homo sapiens\"}), " +
            "      (pe)-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"}) " +
            "WHERE NOT (rle)-[:entityFunctionalStatus]->() " +
            "WITH DISTINCT rle, pe, re, d, COLLECT(DISTINCT {stId: p.stId, displayName: p.displayName}) AS pathways  " +
            "OPTIONAL MATCH (rle)-[:literatureReference]->(lr:LiteratureReference) " +
            "OPTIONAL MATCH (rle)-[:created]->(c:InstanceEdit) " +
            "WITH DISTINCT rle, c, re, d, pathways, COLLECT(DISTINCT lr.pubMedIdentifier) AS pubMedIdentifiers " +
            "RETURN DISTINCT rle.stId as reaction, " +  //DISTINCT CAN BE REMOVED IF
            "       CASE WHEN rle.releaseDate IS NOT NULL THEN rle.releaseDate ELSE c.dateTime END AS releaseDate, " +
            "       re.databaseName AS resource, " +
//            "       CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END AS identifier, " +
            "       re.identifier AS identifier, " +
            "       null AS mutations,  " +
            "       d.identifier AS doid, " +
            "       d.databaseName AS diseaseResource, " +
            "       d.displayName AS disease, " +
            "       \"up_or_down\" AS activity,  " +
            "       pathways, " +
            "       pubMedIdentifiers " +
            "ORDER BY rle.stId";

    public static void export(String path, boolean verbose){
        if (verbose) System.out.print("Running OpenTargets exporter...");

        REACTOME_VERSION = ReactomeGraphCore.getService(GeneralService.class).getDBVersion();

        AdvancedDatabaseObjectService service = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

        String fileName = path + FILE_NAME + REACTOME_VERSION + FILE_EXTENSION;

        int n = 0; Set<String> reactions = new HashSet<>();
        try {
            //noinspection unchecked
            Collection<ReactomeEvidence> evidences = service.getCustomQueryResults(ReactomeEvidence.class, query, Collections.EMPTY_MAP);

            PrintStream ps = new PrintStream(new FileOutputStream(new File(fileName)));
            ObjectMapper mapper = new ObjectMapper();
            for (ReactomeEvidence evidence : evidences) {
                reactions.add(evidence.getReaction());
                EvidenceString evidenceString = new EvidenceString(evidence);
                ps.println(mapper.writeValueAsString(evidenceString));
            }
            n = evidences.size();
        } catch (CustomQueryException | FileNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }
        if (verbose) System.out.println("\rRunning OpenTargets exporter >> Done (" + n + " evidences for " + reactions.size() + " reactions)");
    }
}
