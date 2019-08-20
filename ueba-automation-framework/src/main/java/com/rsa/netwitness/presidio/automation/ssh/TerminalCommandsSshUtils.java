package com.rsa.netwitness.presidio.automation.ssh;


import com.rsa.netwitness.presidio.automation.ssh.client.SshExecutor;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerminalCommandsSshUtils {
//    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TerminalCommandsSshUtils.class.getName());

    static final String targetPath= "/var/netwitness/presidio/batch/";


    /**
     * This file is for sysout redirection - need to consume the process output because of limited pipe for it in linux.
     */
    static File outPutProcessdump;
    public static final String outPutfile ="C:\\Users\\feshed\\datadump.txt";

    /**
     * @param args List of the paramters that the command needs
     * @param command command that the fortscale-collection-1.1.0-SNAPSHOT.jar can run.
     * use of this function should lock like:
     *                runCommand(new ArrayList(Arrays.asList("Scoring","ORACLE","1479859195","12960000"),"scoring",true)
     */

    public static SshResponse runCommand(String command ,boolean wait, String executePath, String... args) {
        List<String> list = new ArrayList<>(Arrays.asList(args));
        return run(list,command , wait, executePath);
    }

    public static SshResponse run(List<String> args, String command, boolean wait, String executePath) {
        if (executePath.equals(""))
            executePath = targetPath;
        return runTerminalCommand(arrangeArgs(args,command) ,executePath ,wait );
    }

    public static String inputArgs(String command, List<String> args){
        for(int i=0;i<args.size();i++){
            command=command.replaceFirst("arg"+i,args.get(i));
        }
        if((command.contains("StartServices")||command.contains("StopServices"))&& args.size()==0)
            command=command.replaceFirst("arg0","");
        return command;
    }


    private static String arrangeArgs(List<String> args, String command){
        String line="";
        for(int i=0;i<args.size();i++){
            line+=" "+args.get(i);
        }
        return command+" "+line;
    }

    private static SshResponse runTerminalCommand(String command, String executePath, boolean wait) {
        return  SshExecutor.executeOnUebaHost(command, false, executePath);
    }

}