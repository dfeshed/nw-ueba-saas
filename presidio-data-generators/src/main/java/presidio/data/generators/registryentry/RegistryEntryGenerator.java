package presidio.data.generators.registryentry;

import presidio.data.domain.FileEntity;
import presidio.data.domain.ProcessEntity;
import presidio.data.domain.RegistryEntry;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import presidio.data.generators.event.process.CertificateIssuerDefaultGenerator;
import presidio.data.generators.event.process.ICertificateIssuerGenerator;
import presidio.data.generators.fileentity.IFileEntityGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;
import presidio.data.generators.processentity.IProcessEntityGenerator;

/**
 * Default generator for Process entity.
 *
 * **/
public class RegistryEntryGenerator implements IRegistryEntryGenerator {

    IStringGenerator registryKeyGenerator;
    IStringGenerator registryKeyGroupGenerator;
    IStringGenerator registryValueNameGenerator;

    public RegistryEntryGenerator() throws GeneratorException {
        registryKeyGenerator = new StringCyclicValuesGenerator(new String[] {"a","b"});
        registryKeyGroupGenerator = new StringCyclicValuesGenerator(new String[] {"g_a","g_b"});
        registryValueNameGenerator = new StringCyclicValuesGenerator(new String[] {"v_a","v_b"});

    }

    public RegistryEntry getNext(){

        String registryKey = getRegistryKeyGenerator().getNext();
        String registryKeyGroup = getRegistryKeyGroupGenerator().getNext();
        String registryValueName = getRegistryValueNameGenerator().getNext();

        return new RegistryEntry(registryKey, registryKeyGroup, registryValueName);
    }

    public IStringGenerator getRegistryKeyGenerator() {
        return registryKeyGenerator;
    }

    public void setRegistryKeyGenerator(IStringGenerator registryKeyGenerator) {
        this.registryKeyGenerator = registryKeyGenerator;
    }

    public IStringGenerator getRegistryKeyGroupGenerator() {
        return registryKeyGroupGenerator;
    }

    public void setRegistryKeyGroupGenerator(IStringGenerator registryKeyGroupGenerator) {
        this.registryKeyGroupGenerator = registryKeyGroupGenerator;
    }

    public IStringGenerator getRegistryValueNameGenerator() {
        return registryValueNameGenerator;
    }

    public void setRegistryValueNameGenerator(IStringGenerator registryValueNameGenerator) {
        this.registryValueNameGenerator = registryValueNameGenerator;
    }
}
