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
    IStringListGenerator processCategoriesGenerator;
    ICertificateIssuerGenerator processCertificateIssuerGenerator;

    public ProcessEntityGenerator() throws GeneratorException {
        processFileGenerator = new ProcessFileEntityGenerator();
        processCategoriesGenerator = new ProcessCategoriesGenerator(new String[] {"SOME_USUAL_PROCESS"});
        processCertificateIssuerGenerator = new CertificateIssuerDefaultGenerator();

    }

    public ProcessEntity getNext(){

        FileEntity processFile = getProcessFileGenerator().getNext();
        List<String> processCategories = getProcessCategoriesGenerator().getNext();
        String processCertificateIssuer = (String) getProcessCertificateIssuerGenerator().getNext();

        return new ProcessEntity(processFile, processCategories, processCertificateIssuer);
    }

    public IFileEntityGenerator getProcessFileGenerator() {
        return processFileGenerator;
    }

    public void setProcessFileGenerator(IFileEntityGenerator processFileGenerator) {
        this.processFileGenerator = processFileGenerator;
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
