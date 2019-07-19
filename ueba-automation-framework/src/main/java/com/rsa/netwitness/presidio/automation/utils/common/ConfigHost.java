package com.rsa.netwitness.presidio.automation.utils.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConfigHost {
    /** NOTE - never push this file **/

    public static boolean isGenerate=false;
    public static String server = getServerHostanme();
    //public static String mockhttpserver = "192.168.0.52";
    //public static String mockhttpserver = "tc-agent9";

    public static String TESTS_HOSTNAME = server;
    public static String FS_URL_PREFIX = "http://" + server + ":8080/";
    public static String MONGO_HOST = server;


    public static String getServerHostanme (){
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("Current Hostname : " + hostname);

        } catch (UnknownHostException e) {
            hostname = "localhost";
            e.printStackTrace();
        }
        return hostname;
    }

}