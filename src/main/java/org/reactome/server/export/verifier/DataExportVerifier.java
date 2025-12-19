package org.reactome.server.export.verifier;

import org.reactome.release.verifier.DefaultVerifier;
import org.reactome.release.verifier.Verifier;

import java.io.IOException;

/**
 * @author Joel Weiser (joel.weiser@oicr.on.ca)
 * Created 1/3/2025
 */
public class DataExportVerifier {

    public static void main(String[] args) throws IOException {
        Verifier verifier = new DefaultVerifier("data_export");
        verifier.parseCommandLineArgs(args);
        verifier.run();
    }
}
