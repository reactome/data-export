package org.reactome.server.export.verifier;

import com.martiansoftware.jsap.*;
import org.reactome.release.verifier.Results;
import org.reactome.release.verifier.TooSmallFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.reactome.release.verifier.TooSmallFile.currentFileIsSmaller;

/**
 * @author Joel Weiser (joel.weiser@oicr.on.ca)
 * Created 1/3/2025
 */
public class Verifier {
    private String outputDirectory;
    private int releaseNumber;

    //private Results results;

    public static void main(String[] args) throws JSAPException, IOException {
        Verifier verifier = new Verifier();
        verifier.parseCommandLineArgs(args);
        verifier.run();
    }

    public void parseCommandLineArgs(String[] args) throws JSAPException {
        SimpleJSAP jsap = new SimpleJSAP(Verifier.class.getName(), "Verify Data Export ran correctly",
            new Parameter[]{
                new FlaggedOption("output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'o', "output", "The folder where the results are written to."),
                new FlaggedOption("releaseNumber", JSAP.INTEGER_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'r', "releaseNumber", "The most recent Reactome release version")
            }
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        this.outputDirectory = config.getString("output");
        this.releaseNumber = config.getInt("releaseNumber");
    }

    public void run() throws IOException {
        Results results = new Results();
        results.addErrorMessages(verifyDataExportRanCorrectly());
        if (!results.hasErrors()) {
            System.out.println("Data Export has run correctly!");
            results.reportInfoMessages();
        } else {
            results.reportErrors();
            System.exit(1);
        }
    }

    private List<String> verifyDataExportRanCorrectly() throws IOException {
        List<String> errorMessages = new ArrayList<>();

        errorMessages.addAll(checkDataExportFolderExists());
        if (errorMessages.isEmpty()) {
            errorMessages.addAll(checkDataExportFilesExist());
            errorMessages.addAll(checkDataExportFileSizesComparedToPreviousRelease());
        }

        return errorMessages;
    }

    private List<String> checkDataExportFolderExists() {
        return !Files.exists(Paths.get(this.outputDirectory)) ?
            List.of(this.outputDirectory + " does not exist; Expected data-export output files at this location") :
            new ArrayList<>();
    }

    private List<String> checkDataExportFilesExist() throws IOException {
        List<String> errorMessages = new ArrayList<>();

        Utils.downloadDataExportFilesAndSizesListFromS3(getPreviousReleaseNumber());
        for (String dataExportFileName : getDataExportFileNames()) {
            Path dataExportFilePath = Paths.get(this.outputDirectory, dataExportFileName);
            if (!Files.exists(dataExportFilePath)) {
                errorMessages.add("File " + dataExportFilePath + " does not exist");
            }
        }

        return errorMessages;
    }

    private List<String> checkDataExportFileSizesComparedToPreviousRelease() throws IOException {
        Utils.downloadDataExportFilesAndSizesListFromS3(getPreviousReleaseNumber());
        List<TooSmallFile> tooSmallFiles = new ArrayList<>();
        for (String dataExportFileName : getDataExportFileNames()) {
            Path dataExportFilePath = Paths.get(this.outputDirectory, dataExportFileName);

            if (Files.exists(dataExportFilePath) && currentFileIsSmaller(dataExportFilePath)) {
                tooSmallFiles.add(new TooSmallFile(dataExportFilePath));
            }
        }

        return tooSmallFiles
            .stream()
            .map(TooSmallFile::toString)
            .collect(Collectors.toList());
    }

    private List<String> getDataExportFileNames() throws IOException {
        return Files.lines(Paths.get(Utils.getDataExportFilesAndSizesListName()))
            .map(this::getFileName)
            .map(this::removeVersionNumberIfPresent)
            .collect(Collectors.toList());
    }

    private String getFileName(String line) {
        return line.split(" ")[1].replace("./", "");
    }

    private String replaceVersionNumberIfPresent(String fileName) {
        return fileName.matches(".*_v\\d+\\.txt") ?
            fileName.replaceFirst("\\d+", String.valueOf(this.releaseNumber)) :
            fileName;
    }

    private String removeVersionNumberIfPresent(String fileName) {
        return (fileName.matches(".*_v\\d+\\.txt")) ?
            fileName.replaceFirst("_v\\d+", "") : fileName;
    }

    private String addVersionNumberToFileName(String fileName) {
        return fileName.replaceFirst(".txt", "_v" + this.releaseNumber + ".txt");
    }

    private int getPreviousReleaseNumber() {
        return this.releaseNumber - 1;
    }
}
