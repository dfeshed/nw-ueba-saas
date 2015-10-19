package fortscale.streaming.service.ipresolving.utils;

import java.net.InetAddress;

/**
 * Created by shays on 15/10/2015.
 */

/**
 * Wrapper for IP address.
 */
public interface FsIpAddressContainer {
    boolean isMatch(String address);
}
