package org.reactome.server.export.verifier;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.reactome.release.verifier.FileUtils.downloadFileFromS3;

/**
 * @author Joel Weiser (joel.weiser@oicr.on.ca)
 * Created 1/4/2025
 */
public class Utils {

    public static void downloadDataExportFilesAndSizesListFromS3(int versionNumber) {
        if (Files.notExists(Paths.get(getDataExportFilesAndSizesListName()))) {
            downloadFileFromS3("reactome", getDataExportFilesAndSizesListPathInS3(versionNumber));
        }
    }

    private static String getDataExportFilesAndSizesListPathInS3(int versionNumber) {
        return String.format("private/releases/%d/data_export/data/%s",
            versionNumber, getDataExportFilesAndSizesListName()
        );
    }

    static String getDataExportFilesAndSizesListName() {
        return "files_and_sizes.txt";
    }
}
