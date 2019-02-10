package presidio.data.generators.processentity;

import presidio.data.domain.FileEntity;
import presidio.data.domain.ProcessEntity;
import presidio.data.generators.common.IStringListGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;

import java.util.List;

public class ProcessEntityGenerator implements IProcessEntityGenerator {

    IFileEntityGenerator processFileGenerator;
    IStringListGenerator processDirectoryGroupsGenerator;
    IStringListGenerator processCategoriesGenerator;


    public ProcessEntityGenerator() {
        processFileGenerator = new ProcessFileEntityGenerator();
        processDirectoryGroupsGenerator = new ProcessDirectoryGroupsGenerator((List<String>) null);
        processCategoriesGenerator = new ProcessCategoriesGenerator((List<String>) null);

    }

    public ProcessEntity getNext(){

        FileEntity processFile = getProcessFileGenerator().getNext();
        List<String> processDirectoryGroups = getProcessDirectoryGroupsGenerator().getNext();
        List<String> processCategories = getProcessCategoriesGenerator().getNext();

        return new ProcessEntity(processFile, processDirectoryGroups, processCategories, "");
    }

    public IFileEntityGenerator getProcessFileGenerator() {
        return processFileGenerator;
    }

    public void setProcessFileGenerator(IFileEntityGenerator processFileGenerator) {
        this.processFileGenerator = processFileGenerator;
    }

    public IStringListGenerator getProcessDirectoryGroupsGenerator() {
        return processDirectoryGroupsGenerator;
    }

    public void setProcessDirectoryGroupsGenerator(IStringListGenerator processDirectoryGroupsGenerator) {
        this.processDirectoryGroupsGenerator = processDirectoryGroupsGenerator;
    }

    public IStringListGenerator getProcessCategoriesGenerator() {
        return processCategoriesGenerator;
    }

    public void setProcessCategoriesGenerator(IStringListGenerator processCategoriesGenerator) {
        this.processCategoriesGenerator = processCategoriesGenerator;
    }
}
