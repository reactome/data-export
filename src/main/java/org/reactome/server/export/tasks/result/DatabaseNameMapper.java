package org.reactome.server.export.tasks.result;

public enum DatabaseNameMapper {

    CHEBI("ChEBI", "chebi"),
    EMBL("EMBL", "embl"),
    ENSEMBL("ENSEMBL", "ensembl"),
    NCBI_NUCLEOTIDE("NCBI Nucleotide", "insdc"),
    NCBI_PROTEIN("NCBI_Protein", "ncbiprotein"),
    PRF("PRF", "ncbiprotein"),
    PUBCHEM_COMPOUND("PubChem Compound","pubchem.compound"),
    UNIPROT("UniProt","uniprot"),
    MIRBASE("miRBase","mirbase");

    private final String displayName;
    private final String name;

    DatabaseNameMapper(String displayName, String name) {
        this.displayName = displayName;
        this.name = name;
    }

    public static String getStandardName(String displayName){
        if (displayName == null) return "null";
        for (DatabaseNameMapper mapper : values()) {
            if (mapper.displayName.equals(displayName)) return mapper.name;
        }
        return displayName.trim().toLowerCase().replaceAll(" ", "");
    }
}
