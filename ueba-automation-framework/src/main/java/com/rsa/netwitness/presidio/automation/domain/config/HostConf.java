package com.rsa.netwitness.presidio.automation.domain.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostConf {
    // TODO: create a generators for expected results generation, the generators should not influence automation from Jenkins, only ran from automation developer IDE
    // Change isGenerate to true if need to create expected results files
    public static boolean isGenerate = false;

    // By default, the presidio host will be the host where we run the tests.
    // In case if need to connect to different host, swipe the comment on two next rows and write the required host.
    //private static String customHost = "mycustomhost";
    public static String customHost = System.getenv("ueba_host");
    public static String outputRestPort = ":8882";

    public static String getServerHostname(){
        InetAddress ip;
        String hostname;
        if (customHost == null) {
            try {
                ip = InetAddress.getLocalHost();
                hostname = ip.getHostName();
            } catch (UnknownHostException e) {
                hostname = "localhost";
                e.printStackTrace();
            }
            return hostname;
        }
        return customHost;
    }

    public static String getServerIpAddress(){
        InetAddress ip;
        String ipAddress;
        if (customHost == null) {
            try {
                ip = InetAddress.getLocalHost();
                ipAddress = ip.getHostAddress();
            } catch (UnknownHostException e) {
                ipAddress = "127.0.0.1";
                e.printStackTrace();
            }
            return ipAddress;
        }
        return customHost;
    }

    public static void setServerHostName(String hostname) {
        if (hostname.length() > 0) {
            System.out.println("Setting custom host:" + hostname);
            customHost = hostname;
        }
    }

    public static String getOutputRestPort(){
        return outputRestPort;
    }

    public static String getOutputRestIpAndPort() {
        return getServerIpAddress() + getOutputRestPort();
    }

}