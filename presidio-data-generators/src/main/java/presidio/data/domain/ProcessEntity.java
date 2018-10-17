package presidio.data.domain;

import java.util.ArrayList;
import java.util.List;

public class ProcessEntity {
    private final static String[] DIRECTORY_GROUPS = {"system32","temp", "downloads"};

    String processFileName;
    String processDirectory;
    List<String> processDirectoryGroups;
    String processCertificateIssuer;

    public ProcessEntity(FileEntity processFile, String processCertificateIssuer) {
        this.processFileName = processFile.getFileName();
        this.processDirectory = processFile.getFilePath();
        this.processDirectoryGroups = getDirectoryGroups(this.processDirectory);
        this.processCertificateIssuer = processCertificateIssuer;
    }

    private List<String> getDirectoryGroups(String directory) {
        List<String> directoryGroups = new ArrayList<>();
        // Simple directory group assignment by substring contained in directory path
        for ( String group : DIRECTORY_GROUPS) {
            if (directory != null && directory.toLowerCase().contains(group)) {
                directoryGroups.add(group);
            }
        }
        return directoryGroups;
    }

    public String getProcessDirectory() {
        return processDirectory;
    }

    public void setProcessDirectory(String processDirectory) {
        this.processDirectory = processDirectory;
    }

    public String getProcessFileName() {
        return processFileName;
    }

    public void setProcessFileName(String processFileName) {
        this.processFileName = processFileName;
    }

    public List<String> getProcessDirectoryGroup() {
        return processDirectoryGroups;
    }

    public void setProcessDirectoryGroup(List<String> processDirectoryGroup) {
        this.processDirectoryGroups = processDirectoryGroup;
    }

    public String getProcessCertificateIssuer() {
        return processCertificateIssuer;
    }

    public void setProcessCertificateIssuer(String processCertificateIssuer) {
        this.processCertificateIssuer = processCertificateIssuer;
    }

    @Override
    public String toString() {
        return "ProcessEntity{" +
                "processDirectory='" + processDirectory + '\'' +
                ", processFileName='" + processFileName + '\'' +
                ", processDirectoryGroup='" + processDirectoryGroups + '\'' +
                ", processCertificateIssuer='" + processCertificateIssuer + '\'' +
                '}';
    }
}
