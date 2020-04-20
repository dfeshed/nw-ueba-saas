package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.CyclicValuesGenerator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import presidio.data.generators.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StaticIPMachineGenerator extends CyclicValuesGenerator<MachineEntity> implements IMachineGenerator {

    /**
     * Generates MachineEntity objects from given pairs of hostname and ip address
     * @param pairs
     */
    public StaticIPMachineGenerator(List<Pair<String, String>> pairs)  {
        super(buildMachineList(pairs));
    }

    private static MachineEntity[] buildMachineList(List<Pair<String, String>> pairs) {
        MachineEntity[] machineEntities = new MachineEntity[pairs.size()];

        int i = 0;
        for(Pair<String, String> pair : pairs) {
            machineEntities[i++] = new MachineEntity(pair.getLeft(), pair.getRight(), "","");
        }
        return machineEntities;
    }

    /**
     * Generates MachineEntity objects by building:
     *     - machine name strings from user name
     *     - host ips as constant "192.168.0." + incremental int value
     *
     * @param username
     * @param numOfMachines
     */
    public StaticIPMachineGenerator(String username, int numOfMachines)  {
        super(buildMachineListByUserList(username, numOfMachines));
    }

    private static MachineEntity[] buildMachineListByUserList(String username, int numOfMachines) {
        String[] machineNames = StringUtils.buildUniqueAlphabetStrings(username, "", numOfMachines);
        IPIterator ipIterator = new IPIterator("192.168.0.0");
        List<Pair<String,String>> machineAndIpPairList= new ArrayList<>();
        for(String machineName: machineNames){
            machineAndIpPairList.add(new ImmutablePair<>(machineName,ipIterator.next()));
        }
        return buildMachineList(machineAndIpPairList);
    }



    public static class IPIterator{
        private Integer[] ipAddressArray = new Integer[4];

        public IPIterator(String ipAddress){
            String[] ipAddressSplit = ipAddress.split("\\.");
            ipAddressArray[0] = Integer.decode(ipAddressSplit[0]);
            ipAddressArray[1] = Integer.decode(ipAddressSplit[1]);
            ipAddressArray[2] = Integer.decode(ipAddressSplit[2]);
            ipAddressArray[3] = Integer.decode(ipAddressSplit[3]);
        }

        public String next(){
            incrementD();
            return toString();
        }

        private void incrementD(){
            if(ipAddressArray[3] == 255){
                ipAddressArray[3] = 0;
                incrementC();
            }else {
                ipAddressArray[3]++;
            }
        }

        private void incrementC(){
            if(ipAddressArray[2] == 255){
                ipAddressArray[2] = 0;
                incrementB();
            }else {
                ipAddressArray[2]++;
            }
        }

        private void incrementB(){
            if(ipAddressArray[1] == 255){
                ipAddressArray[1] = 0;
                incrementA();
            }else {
                ipAddressArray[1]++;
            }
        }

        private void incrementA(){
            if(ipAddressArray[0] == 255){
                ipAddressArray[0] = 0;
                incrementD();
            }else {
                ipAddressArray[0]++;
            }
        }

        public String toString(){
            return org.apache.commons.lang.StringUtils.join(ipAddressArray,'.');
        }
    }
}
