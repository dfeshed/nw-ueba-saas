package presidio.data.generators.utils;

import org.apache.commons.lang.StringUtils;

public class IPIterator{
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