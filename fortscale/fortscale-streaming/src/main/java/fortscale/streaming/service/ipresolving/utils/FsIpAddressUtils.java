package fortscale.streaming.service.ipresolving.utils;

import org.apache.http.conn.util.InetAddressUtils;

/**
 * Created by shays on 15/10/2015.
 */
public class FsIpAddressUtils {

    /**
     * Static method to fetch the appropiate imelementation of FsIpAddressContainer.
     *
     * @param ipAddress - string which contains single IP (I.E 1.1.1.1) or range of IPs
     *                  (I.E. 1.1.1.1 - 3.3.3.3)
     * @return FsRangeIpAddressContainer or FsSingleIpAddressContainer according to input
     */
    public static FsIpAddressContainer getIpAddressContainer(String ipAddress){

        String fromIp;
        String toIp;
        //Check if this is expected bo be range of IP
        if (ipAddress.contains("-")){
            String[] toFrom = ipAddress.split("-");
            fromIp = toFrom[0].trim();
            toIp = toFrom[1].trim();
            //Check if both IPv6 or both IPv4
            if (InetAddressUtils.isIPv4Address(fromIp) &&
                    (InetAddressUtils.isIPv4Address(toIp))
                    || InetAddressUtils.isIPv6Address(fromIp) &&
                            (InetAddressUtils.isIPv6Address(toIp))) {

                //Create FsRangeIpAddressContainer
                return new FsRangeIpAddressContainer(fromIp, toIp);
            } else {
                throw new IllegalArgumentException("IPS are not match "+ipAddress);
            }

        } else{
            ipAddress = ipAddress.trim();
            if (InetAddressUtils.isIPv4Address(ipAddress) ||
                    InetAddressUtils.isIPv6Address(ipAddress)){
                //Create FsSingleIpAddressContainer
                return new FsSingleIpAddressContainer(ipAddress);
            }  else {
                throw new IllegalArgumentException("IPS are not match "+ipAddress);
            }

        }
    }

    public static long ipToLong(String ipAddress) {

        String[] ipAddressInArray = ipAddress.split("\\.");

        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {

            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);
            result += ip * Math.pow(256, power);

        }

        return result;
    }
}
