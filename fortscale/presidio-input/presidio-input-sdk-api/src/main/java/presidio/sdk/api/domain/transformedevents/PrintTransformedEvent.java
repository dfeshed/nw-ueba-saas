package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.PrintRawEvent;

public class PrintTransformedEvent extends PrintRawEvent {

    public static final String SRC_MACHINE_CLUSTER_FIELD_NAME = "srcMachineCluster";
    public static final String SRC_FOLDER_PATH_FIELD_NAME = "srcFolderPath";
    public static final String SRC_FILE_EXTENSION_FIELD_NAME = "srcFileExtension";
    public static final String PRINTER_CLUSTER_FIELD_NAME = "printerCluster";

    private String srcMachineCluster;
    private String srcFolderPath;
    private String srcFileExtension;
    private String printerCluster;

    public PrintTransformedEvent(PrintRawEvent other) {
        super(other);
    }

    public String getSrcMachineCluster() {
        return srcMachineCluster;
    }

    public void setSrcMachineCluster(String srcMachineCluster) {
        this.srcMachineCluster = srcMachineCluster;
    }

    public String getSrcFolderPath() {
        return srcFolderPath;
    }

    public void setSrcFolderPath(String srcFolderPath) {
        this.srcFolderPath = srcFolderPath;
    }

    public String getSrcFileExtension() {
        return srcFileExtension;
    }

    public void setSrcFileExtension(String srcFileExtension) {
        this.srcFileExtension = srcFileExtension;
    }

    public String getPrinterCluster() {
        return printerCluster;
    }

    public void setPrinterCluster(String printerCluster) {
        this.printerCluster = printerCluster;
    }
}
