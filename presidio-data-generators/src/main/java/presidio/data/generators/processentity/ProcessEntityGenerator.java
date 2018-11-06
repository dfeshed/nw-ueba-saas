package presidio.data.generators.processentity;

import presidio.data.domain.FileEntity;
import presidio.data.domain.ProcessEntity;
import presidio.data.generators.common.*;
import presidio.data.generators.event.process.CertificateIssuerDefaultGenerator;
import presidio.data.generators.event.process.ICertificateIssuerGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * Default generator for Process entity.
 *
 * **/
public class ProcessEntityGenerator implements IProcessEntityGenerator {

    IFileEntityGenerator processFileGenerator;
    IStringListGenerator processDirectoryGroupsGenerator;
    IStringListGenerator processCategoriesGenerator;
    ICertificateIssuerGenerator processCertificateIssuerGenerator;

    public ProcessEntityGenerator() throws GeneratorException {
        processFileGenerator = new ProcessFileEntityGenerator();
        processDirectoryGroupsGenerator = new ProcessDirectoryGroupsGenerator(new String[] {"SOME_USUAL_DIR_GROUP"});
        processCategoriesGenerator = new ProcessCategoriesGenerator(new String[] {"SOME_USUAL_PROCESS"});
        processCertificateIssuerGenerator = new CertificateIssuerDefaultGenerator();

    }

    public ProcessEntity getNext(){

        FileEntity processFile = getProcessFileGenerator().getNext();
        List<String> processDirectoryGroups = getProcessDirectoryGroupsGenerator().getNext();
        List<String> processCategories = getProcessCategoriesGenerator().getNext();
        String processCertificateIssuer = getProcessCertificateIssuerGenerator().getNext();

        return new ProcessEntity(processFile, processDirectoryGroups, processCategories, processCertificateIssuer);
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

    public void setProcessCertificateIssuerGenerator(ICertificateIssuerGenerator processCertificateIssuerGenerator) {
        this.processCertificateIssuerGenerator = processCertificateIssuerGenerator;
    }

    public ICertificateIssuerGenerator getProcessCertificateIssuerGenerator() {
        return processCertificateIssuerGenerator;
    }

}
