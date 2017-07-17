package presidio.data.generators.machine;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.domain.Machine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import presidio.data.generators.utils.StringGeneratorUtils;

import java.util.ArrayList;
import java.util.List;

public class StaticIPMachineGenerator extends AbstractCyclicValuesGenerator<Machine> implements IMachineGenerator {

    /**
     * Generates Machine objects from given pairs of hostname and ip address
     * @param pairs
     */
    public StaticIPMachineGenerator(List<Pair<String, String>> pairs)  {
        super(buildMachineList(pairs));
    }

    private static Machine[] buildMachineList(List<Pair<String, String>> pairs) {
        Machine[] machines = new Machine[pairs.size()];

        int i = 0;
        for(Pair<String, String> pair : pairs) {
            machines[i++] = new Machine(pair.getLeft(), pair.getRight());
        }
        return machines;
    }

    /**
     * Generates Machine objects by building:
     *     - machine name strings from user name
     *     - host ips as constant "192.168.0." + incremental int value
     *
     * @param username
     * @param numOfMachines
     */
    public StaticIPMachineGenerator(String username, int numOfMachines)  {
        super(buildMachineListByUserList(username, numOfMachines));
    }

    private static Machine[] buildMachineListByUserList(String username, int numOfMachines) {
        String[] machineNames = StringGeneratorUtils.buildUniqueAlphabetStrings(username, "", numOfMachines);
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
            return StringUtils.join(ipAddressArray,'.');
        }
    }
}
