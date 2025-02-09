package org.reactome.server.export.mapping;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
abstract class MappingQueries {

    static String queryResourceToLLP(String resource) {
        return " MATCH (rd:ReferenceDatabase)<-[:referenceDatabase]-(n) " +
                "WHERE rd.displayName =~ \"" + resource + "\" " +
                "WITH DISTINCT n " +
                "MATCH (n)<-[:referenceEntity|referenceSequence|crossReference|referenceGene*]-(pe:PhysicalEntity) " +
                "WITH DISTINCT n, pe " +
                "MATCH (pe)<-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]-(rle:ReactionLikeEvent) " +
                "WITH n, COLLECT(DISTINCT rle) AS rles " + //At this point we need to narrow down the found RLES by taking each one only once and then
                "UNWIND rles AS rle " +                    //unwind the list to start the second part with fewer nodes in order to improve performance
                "MATCH (rle)<-[:hasEvent]-(llp:Pathway) " +
                "RETURN DISTINCT n.identifier AS Identifier," +
                "                n.variantIdentifier AS variantIdentifier," +
                "                llp.stId AS Pathway_ID," +
                "                llp.displayName as Pathway_Name," +
                "                rle.isInferred AS Evidence_Code," +
                "                llp.speciesName as Species " +
                "ORDER BY Identifier, variantIdentifier, Pathway_ID";
    }

    static String queryResourceToAllPathways(String resource) {
        return " MATCH (rd:ReferenceDatabase)<-[:referenceDatabase]-(n) " +
                "WHERE rd.displayName =~ \"" + resource + "\" " +
                "WITH DISTINCT n " +
                "MATCH (n)<-[:referenceEntity|referenceSequence|crossReference|referenceGene*]-(pe:PhysicalEntity) " +
                "WITH DISTINCT n, pe " +
                "MATCH (pe)<-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]-(rle:ReactionLikeEvent) " +
                "WITH DISTINCT n, rle " +
                "MATCH (rle)<-[:hasEvent*]-(p:Pathway) " +
                "RETURN DISTINCT n.identifier AS Identifier, " +
                "                n.variantIdentifier AS variantIdentifier, " +
                "                p.stId AS Pathway_ID, " +
                "                p.displayName as Pathway_Name, " +
                "                rle.isInferred AS Evidence_Code, " +
                "                p.speciesName as Species " +
                "ORDER BY Identifier, variantIdentifier, Pathway_ID";
    }

    static String queryResourceToReactions(String resource) {
        return " MATCH (rd:ReferenceDatabase)<-[:referenceDatabase]-(n) " +
                "WHERE rd.displayName =~ \"" + resource + "\" " +
                "WITH DISTINCT n " +
                "MATCH (n)<-[:referenceEntity|referenceSequence|crossReference|referenceGene*]-(pe:PhysicalEntity) " +
                "WITH DISTINCT n, pe " +
                "MATCH (pe)<-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]-(rle:ReactionLikeEvent) " +
                "RETURN DISTINCT n.identifier AS Identifier," +
                "                n.variantIdentifier AS variantIdentifier," +
                "                rle.stId AS Reaction_ID," +
                "                rle.displayName as Reaction_Name," +
                "                rle.isInferred AS Evidence_Code," +
                "                rle.speciesName as Species " +
                "ORDER BY Identifier, variantIdentifier, Reaction_ID";
    }



    static String queryResourceToPEAndLLP(String resource) {
        return " MATCH (rd:ReferenceDatabase)<-[:referenceDatabase]-(n) " +
                "WHERE rd.displayName =~ \"" + resource + "\" " +
                "WITH DISTINCT n " +
                "MATCH (n)<-[:referenceEntity|referenceSequence|crossReference|referenceGene*]-(pe:PhysicalEntity) " +
                "WITH DISTINCT n, pe " +
                "MATCH (pe)<-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]-(rle:ReactionLikeEvent) " +
                "WITH DISTINCT n, pe, rle " +
                "MATCH (rle)<-[:hasEvent]-(llp:Pathway) " +
                "RETURN DISTINCT n.identifier AS Identifier," +
                "                n.variantIdentifier AS variantIdentifier," +
                "                pe.stId AS Entity_ID," +
                "                pe.displayName AS Entity_Name," +
                "                llp.stId AS Pathway_ID," +
                "                llp.displayName as Pathway_Name," +
                "                rle.isInferred AS Evidence_Code," +
                "                llp.speciesName as Species " +
                "ORDER BY Identifier, variantIdentifier, Pathway_ID";
    }

    static String queryResourceToPEAndAllPathways(String resource) {
        return " MATCH (rd:ReferenceDatabase)<-[:referenceDatabase]-(n) " +
                "WHERE rd.displayName =~ \"" + resource + "\" " +
                "WITH DISTINCT n " +
                "MATCH (n)<-[:referenceEntity|referenceSequence|crossReference|referenceGene*]-(pe:PhysicalEntity) " +
                "WITH DISTINCT n, pe " +
                "MATCH (pe)<-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]-(rle:ReactionLikeEvent) " +
                "WITH DISTINCT n, pe, rle " +
                "MATCH (rle)<-[:hasEvent*]-(p:Pathway) " +
                "RETURN DISTINCT n.identifier AS Identifier, " +
                "                n.variantIdentifier AS variantIdentifier, " +
                "                pe.stId AS Entity_ID," +
                "                pe.displayName AS Entity_Name," +
                "                p.stId AS Pathway_ID, " +
                "                p.displayName as Pathway_Name, " +
                "                rle.isInferred AS Evidence_Code, " +
                "                p.speciesName as Species " +
                "ORDER BY Identifier, variantIdentifier, Pathway_ID";
    }

    static String queryResourceToPEAndReactions(String resource) {
        return " MATCH (rd:ReferenceDatabase)<-[:referenceDatabase]-(n) " +
                "WHERE rd.displayName =~ \"" + resource + "\" " +
                "WITH DISTINCT n " +
                "MATCH (n)<-[:referenceEntity|referenceSequence|crossReference|referenceGene*]-(pe:PhysicalEntity) " +
                "WITH DISTINCT n, pe " +
                "MATCH (pe)<-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]-(rle:ReactionLikeEvent) " +
                "RETURN DISTINCT n.identifier AS Identifier," +
                "                n.variantIdentifier AS variantIdentifier," +
                "                pe.stId AS Entity_ID," +
                "                pe.displayName AS Entity_Name," +
                "                rle.stId AS Reaction_ID," +
                "                rle.displayName as Reaction_Name," +
                "                rle.isInferred AS Evidence_Code," +
                "                rle.speciesName as Species " +
                "ORDER BY Identifier, variantIdentifier, Reaction_ID";
    }
}
