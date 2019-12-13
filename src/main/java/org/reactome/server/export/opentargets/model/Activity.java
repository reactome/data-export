package org.reactome.server.export.opentargets.model;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public enum Activity {
    DECREASED_TRANSCRIPT_LEVEL("http://identifiers.org/cttv.activity/decreased_transcript_level"),
    DECREASED_PRODUCT_LEVEL("http://identifiers.org/cttv.activity/decreased_translational_product_level"),
    DRUG_NEGATIVE_MODULATOR("http://identifiers.org/cttv.activity/drug_negative_modulator"),
    DRUG_POSITIVE_MODULATOR("http://identifiers.org/cttv.activity/drug_positive_modulator"),
    GAIN_OF_FUNCTION("http://identifiers.org/cttv.activity/gain_of_function"),
    INCREASED_TRANSCRIPT_LEVEL("http://identifiers.org/cttv.activity/increased_transcript_level"),
    INCREASED_TRANSLATIONAL_PRODUCT_LEVEL ("http://identifiers.org/cttv.activity/increased_translational_product_level"),
    LOSS_OF_FUNCTION ("http://identifiers.org/cttv.activity/loss_of_function"),
    PARTIAL_LOSS_OF_FUNCTION("http://identifiers.org/cttv.activity/partial_loss_of_function"),
    UP_OR_DOWN("http://identifiers.org/cttv.activity/up_or_down"),
    UP("http://identifiers.org/cttv.activity/up"),
    DOWN("http://identifiers.org/cttv.activity/down"),
    TOLERATED("http://identifiers.org/cttv.activity/tolerated"),
    PREDICTED("http://identifiers.org/cttv.activity/predicted"),
    DAMAGING("http://identifiers.org/cttv.activity/damaging"),
    DAMAGING_TO_TARGET("http://identifiers.org/cttv.activity/damaging_to_target"),
    PREDICTED_TOLERATED("http://identifiers.org/cttv.activity/predicted_tolerated"),
    PREDICTED_DAMAGING("http://identifiers.org/cttv.activity/predicted_damaging"),
    TOLERATED_BY_TARGET("http://identifiers.org/cttv.activity/tolerated_by_target");

    private String activity;

    Activity(String activity) {
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public static Activity getActivity(String activity) {
        switch (activity) {
            case "decreased_transcript_level":
                return Activity.DECREASED_TRANSCRIPT_LEVEL;
            case "gain_of_function":
                return Activity.GAIN_OF_FUNCTION;
            case "loss_of_function":
                return Activity.LOSS_OF_FUNCTION;
            case "partial_loss_of_function":
                return Activity.PARTIAL_LOSS_OF_FUNCTION;
            case "up_or_down":
                return Activity.UP_OR_DOWN;
            case "increased_translational_product_level":
                return Activity.INCREASED_TRANSLATIONAL_PRODUCT_LEVEL;
            default:
                System.err.println("Not activity found for [" + activity + "]. Please open Activity.java and add it.");
                throw new IllegalArgumentException("Not activity found for [" + activity + "]. Please open Activity.java and add it.");
        }
    }
}
