package presidio.data.generators.processentity;

import presidio.data.domain.FileEntity;
import presidio.data.domain.ProcessEntity;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.process.CertificateIssuerDefaultGenerator;
import presidio.data.generators.event.process.ICertificateIssuerGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;

import java.util.List;

/**
 * Default generator for Process entity.
 *
 * **/
public class ProcessEntityGenerator implements IProcessEntityGenerator {

    IFileEntityGenerator processFileGenerator;
    ICertificateIssuerGenerator processCertificateIssuerGenerator;

    public ProcessEntityGenerator() throws GeneratorException {
        processFileGenerator = new ProcessFileEntityGenerator();
        processCertificateIssuerGenerator = new CertificateIssuerDefaultGenerator();

    }

    public ProcessEntity getNext(){

        FileEntity processFile = getProcessFileGenerator().getNext();
        String processCertificateIssuer = (String) getProcessCertificateIssuerGenerator().getNext();

        return new ProcessEntity(processFile, processCertificateIssuer);
    }

    public IFileEntityGenerator getProcessFileGenerator() {
        return processFileGenerator;
    }

    public void setProcessFileGenerator(IFileEntityGenerator processFileGenerator) {
        this.processFileGenerator = processFileGenerator;
    }

    public ICertificateIssuerGenerator getProcessCertificateIssuerGenerator() {
        return processCertificateIssuerGenerator;
    }

    public void setProcessCertificateIssuerGenerator(CertificateIssuerDefaultGenerator processCertificateIssuerGenerator) {
        this.processCertificateIssuerGenerator = processCertificateIssuerGenerator;
    }
}
