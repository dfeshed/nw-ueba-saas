package com.rsa.netwitness.presidio.automation.common.helpers;

import presidio.data.domain.MachineEntity;

public class IpAddressUtils {

    public static String buildFQDN(MachineEntity machine) {
        String machineId = machine.getMachineId();
        if (machineId == null) return null;
        return machineId + ((IpAddressUtils.verifyValidIP(machineId))?"": "." + machine.getDomainFQDN());
    }

    public static boolean verifyValidIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
