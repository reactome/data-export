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
import java.util.*;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
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
            "      (efs)-[:hasComponent|hasMember|hasCandidate|repeatedUnit|diseaseEntity|proteinMarker|RNAMarker*]->(pe:PhysicalEntity)-[:species]->(:Species{displayName:\"Homo sapiens\"}), " +
            "      (pe)-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"}) " +
            //pe is meant to differentiate the mutations per reference entity
            "WITH DISTINCT rle, pe, re, d, fst, COLLECT(DISTINCT {stId: p.stId, displayName: p.displayName}) AS pathways " +
            "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(gmr:GeneticallyModifiedResidue) " +
            "OPTIONAL MATCH (rle)-[:literatureReference]->(lr:LiteratureReference) " +
            "OPTIONAL MATCH (rle)-[:created]->(c:InstanceEdit) " +
            //pe is meant to differentiate the mutations per reference entity -> DO NOT DELETE even though isn't used below
            "WITH DISTINCT rle, c, pe, re, COLLECT(DISTINCT gmr.displayName) AS relMutations, d, fst, pathways, COLLECT(DISTINCT lr.pubMedIdentifier) AS pubMedIdentifiers " +
            "ORDER BY rle.stId " +
            "UNWIND (CASE relMutations WHEN [] then [null] else relMutations end) as mutation " +
            "RETURN DISTINCT {stId: rle.stId, displayName: rle.displayName} AS reaction, " +
            "       re.databaseName AS resource, " +
            "       re.identifier AS identifier, " +
            "       COLLECT(DISTINCT mutation) AS mutations, " +
            "       d.identifier AS doid, " +
            "       d.databaseName AS diseaseResource, " +
            "       d.displayName AS disease, " +
            "       fst.displayName AS activity, " +
            "       pathways,  " +
            "       pubMedIdentifiers " +
            "UNION " +
            "MATCH (d:Disease)<-[:disease]-(rle:ReactionLikeEvent)<-[:hasEvent]-(p:Pathway), " +
            "      (rle)-[:input|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]->(pe:PhysicalEntity)-[:species]->(:Species{displayName:\"Homo sapiens\"}), " +
            "      (pe)-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"}) " +
            "WHERE NOT (rle)-[:entityFunctionalStatus]->() " +
            "WITH DISTINCT rle, pe, re, d, COLLECT(DISTINCT {stId: p.stId, displayName: p.displayName}) AS pathways  " +
            "OPTIONAL MATCH (rle)-[:literatureReference]->(lr:LiteratureReference) " +
            "OPTIONAL MATCH (rle)-[:created]->(c:InstanceEdit) " +
            "WITH DISTINCT rle, c, re, d, pathways, COLLECT(DISTINCT lr.pubMedIdentifier) AS pubMedIdentifiers " +
            "ORDER BY rle.stId " +
            "RETURN DISTINCT {stId: rle.stId, displayName: rle.displayName} AS reaction, " +
            "       re.databaseName AS resource, " +
            "       re.identifier AS identifier, " +
            "       null AS mutations,  " +
            "       d.identifier AS doid, " +
            "       d.databaseName AS diseaseResource, " +
            "       d.displayName AS disease, " +
            "       \"up_or_down\" AS activity,  " +
            "       pathways, " +
            "       pubMedIdentifiers ";

    public static void export(String path, boolean verbose){
        if (verbose) System.out.print("Running OpenTargets exporter...");

        REACTOME_VERSION = ReactomeGraphCore.getService(GeneralService.class).getDBInfo().getVersion();

        AdvancedDatabaseObjectService service = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

        String fileName = path + FILE_NAME + REACTOME_VERSION + FILE_EXTENSION;

        int evidences = 0;
        Map<String, Integer> reactionPerDiseases = new HashMap<>();
        Set<String> uniqueReactions = new HashSet<>();
        try {
            //noinspection unchecked
            Collection<ReactomeEvidence> reactomeEvidencesQueryResult = service.getCustomQueryResults(ReactomeEvidence.class, query, Collections.EMPTY_MAP);

            PrintStream ps = new PrintStream(new FileOutputStream(new File(fileName)));
            ObjectMapper mapper = new ObjectMapper();
            for (ReactomeEvidence reactomeEvidence : reactomeEvidencesQueryResult) {
                EvidenceString evidenceString = new EvidenceString(reactomeEvidence);
                ps.println(mapper.writeValueAsString(evidenceString));
                evidences += reactomeEvidence.getMutations() != null ? reactomeEvidence.getMutations().size() : 0;
                // counting unique reactions
                uniqueReactions.add(reactomeEvidence.getReaction().getStId());
                // Same reaction appears more than once if they refer to different disease
                reactionPerDiseases.merge(reactomeEvidence.getReaction().getStId()+reactomeEvidence.getSourceDiseaseIdentifier(), 1, Integer::sum);
            }

            int extras = 0;
            for (String reactionPlusDiseaseKey : reactionPerDiseases.keySet()) {
                int sum = reactionPerDiseases.get(reactionPlusDiseaseKey);
                // If reaction ID has only one entry, it has been counted already. Otherwise sum different DOID.
                extras += (sum <= 1) ? 0 : (sum - 1);
            }

            // adding up
            evidences += extras;

            ps.close();

        } catch (CustomQueryException | FileNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }

        if (verbose) System.out.println("\rRunning OpenTargets exporter >> Done (" + evidences + " evidences for " + uniqueReactions.size() + " reactions)");

    }
}
