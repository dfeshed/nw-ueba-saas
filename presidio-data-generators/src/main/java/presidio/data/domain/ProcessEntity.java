package presidio.data.domain;

import java.util.*;

public class ProcessEntity {
    private final static String[] DIRECTORY_GROUPS = {"system32","temp", "downloads"};

    String processFileName;
    String processDirectory;
    List<String> processDirectoryGroups;
    List<String> processCategories;
    String processCertificateIssuer;

    private static final Map<String, List<String>> PROCESS_CATEGORIES_MAP = createCategoriesMap();
    private static Map<String, List<String>> createCategoriesMap()
    {
        Map<String,List<String>> categoriesMap = new HashMap<String,List<String>>();
        categoriesMap.put("word.exe", Arrays.asList("OFFICE", "WORD_PROCESSOR"));
        categoriesMap.put("excel.exe", Arrays.asList("OFFICE", "SPREADSHEET"));
        return categoriesMap;
    }

    public ProcessEntity(FileEntity processFile, String processCertificateIssuer) {
        this.processFileName = processFile.getFileName();
        this.processDirectory = processFile.getFilePath();
        this.processDirectoryGroups = assignDirectoryGroups(this.processDirectory);
        this.processCategories = assignProcessCategories(this.processFileName);
        this.processCertificateIssuer = processCertificateIssuer;
    }

    private List<String> assignProcessCategories(String processFileName) {

        return PROCESS_CATEGORIES_MAP.get(processFileName);
    }

    private List<String> assignDirectoryGroups(String directory) {
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

    public List<String> getProcessDirectoryGroups() {
        return processDirectoryGroups;
    }

    public void setProcessDirectoryGroups(List<String> processDirectoryGroup) {
        this.processDirectoryGroups = processDirectoryGroup;
    }

    public String getProcessCertificateIssuer() {
        return processCertificateIssuer;
    }

    public void setProcessCertificateIssuer(String processCertificateIssuer) {
        this.processCertificateIssuer = processCertificateIssuer;
    }

    public List<String> getProcessCategories() {
        return processCategories;
    }

    public void setProcessCategories(List<String> processCategories) {
        this.processCategories = processCategories;
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
