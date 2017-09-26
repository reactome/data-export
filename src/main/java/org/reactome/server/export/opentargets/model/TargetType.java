package org.reactome.server.export.opentargets.model;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public enum TargetType {

    Gene ("http://identifiers.org/cttv.target/gene_evidence"),
    Gene_allele ("http://identifiers.org/cttv.target/gene_allele_evidence"),
    Gene_in_ld_region ("http://identifiers.org/cttv.target/gene_in_ld_region_evidence"),
    Gene_variant ("http://identifiers.org/cttv.target/gene_variant_evidence"),
    Gene_in_epigenetic_regulation_complex ("http://identifiers.org/cttv.target/gene_in_epigenetic_regulation_complex_evidence"),
    Transcript ("http://identifiers.org/cttv.target/transcript_evidence"),
    Transcript_isoform ("http://identifiers.org/cttv.target/transcript_isoform_evidence"),
    Protein ("http://identifiers.org/cttv.target/protein_evidence"),
    Protein_complex_homopolymer ("http://identifiers.org/cttv.target/protein_complex_homopolymer_evidence"),
    Protein_complex_heteropolymer ("http://identifiers.org/cttv.target/protein_complex_heteropolymer_evidence"),
    Protein_isoform ("http://identifiers.org/cttv.target/protein_isoform_evidence"),
    Protein_signaling_pathway ("http://identifiers.org/cttv.target/protein_signaling_pathway_evidence");

    private String context;

    TargetType(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }
}
