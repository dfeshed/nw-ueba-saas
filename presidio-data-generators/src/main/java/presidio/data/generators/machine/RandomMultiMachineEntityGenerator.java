package presidio.data.generators.machine;

import org.springframework.util.Assert;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import presidio.data.generators.common.random.RandomIpGenerator;
import presidio.data.generators.common.random.RandomNumberedStringGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomMultiMachineEntityGenerator implements IMachineGenerator {

    private static final String OS_VERSION = "Windows Server 2016 Datacenter";

    private IStringGenerator machineIPGenerator;
    private IStringGenerator osVersionGenerator;
    private List<String> domains;
    private int numOfClusters;
    private String clusterPrefix;
	private List<String> generatedClusters;
    private Random random;
    private RandomStringGenerator randomStringClusterGenerator;
    private RandomNumberedStringGenerator machineNameGenerator;

    public RandomMultiMachineEntityGenerator(List<String> domains, int numOfClusters, String clusterPrefix,
                                             int numOfMachinesPerCluster, String machineNamePrefix){
        Assert.isTrue(numOfClusters > 0, " numOfCluster should be positive.");
        Assert.hasText(clusterPrefix,"clusterPrefix should not be blank");

        this.domains = domains;
        this.numOfClusters = numOfClusters;
        this.clusterPrefix = clusterPrefix;
		this.generatedClusters = new ArrayList();
        machineIPGenerator = new RandomIpGenerator();
        osVersionGenerator = new StringCyclicValuesGenerator(OS_VERSION);

        random = new Random();
        randomStringClusterGenerator = new RandomStringGenerator(20);
        machineNameGenerator = new RandomNumberedStringGenerator(numOfMachinesPerCluster, 0, machineNamePrefix);
    }

    @Override
    public MachineEntity getNext() {
        String machineDomain = randDomain();
        String machineNameRegexCluster = randCluster(machineDomain);
        String machineName = machineNameGenerator.getNext() + "_" + machineNameRegexCluster;

        String machineId = machineName;
        String machineIP = machineIPGenerator.getNext();
        String osVersion = OS_VERSION;
        //TODO: cache the following values for performance.
        String machineDomainDN = String.format("DC=%s,DC=quest,DC=azure,DC=ca", machineDomain);
        String domainFQDN = String.format("%s.quest.azure.ca", machineDomain);

        return new MachineEntity(machineId, machineIP, machineNameRegexCluster, machineDomain,
                machineDomainDN, domainFQDN, osVersion);
    }

    private String randCluster(String domain){
		int index = random.nextInt(numOfClusters);
		String cluster = null;
		if(index < generatedClusters.size()){
		    cluster = generatedClusters.get(index);
        } else{
		    cluster = randomStringClusterGenerator.getNext();
		    generatedClusters.add(cluster);
        }

        return cluster + "_" +domain;
    }

    private String randDomain(){
        if(domains.isEmpty()){
            return null;
        }
        int index = random.nextInt(domains.size());
        return domains.get(index);
    }
}
