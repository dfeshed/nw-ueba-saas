package com.rsa.netwitness.presidio.automation.utils.common;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TerminalCommands {
//    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TerminalCommands.class.getName());

    static final String targetPath= "/var/netwitness/presidio/batch/";


    /**
     * This file is for sysout redirection - need to consume the process output because of limited pipe for it in linux.
     */
    static File outPutProcessdump;
    public static final String outPutfile ="/tmp/datadump.txt";

    /**
     * @param args List of the paramters that the command needs
     * @param command command that the fortscale-collection-1.1.0-SNAPSHOT.jar can run.
     * use of this function should lock like:
     *                runCommand(new ArrayList(Arrays.asList("Scoring","ORACLE","1479859195","12960000"),"scoring",true)
     */

    public static Process runCommand(String command ,boolean wait, String executePath, String... args) {
        List<String> list = new ArrayList<>(Arrays.asList(args));
        return run(list,command , wait, executePath);
    }

    public static Process run(List<String> args, String command, boolean wait, String executePath) {
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


    public static String arrangeArgs(List<String> args ,String command){
        String line="";
        for(int i=0;i<args.size();i++){
            line+=" "+args.get(i);
        }
        return command+" "+line;
    }

    public static Process runTerminalCommand(String command, String executePath, boolean wait) {
        initOutPutProcessdump();
        FileCommands.writeToFile(outPutfile,"************************** \n"+command);

        System.out.println("Going to run the command: \n" + command);
        long commandRunTime;
        try {
            SSHManager sshManager = SSHManagerSingleton.INSTANCE.getSshManager();

            if(sshManager.getStrConnectionIP().equals("localhost")) {
                System.out.println("Execute path is: " + executePath);
                File folder = new File(executePath);
                String[] args = new String[]{"bash", "-c", command};
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                processBuilder.redirectErrorStream(true);
                processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(outPutProcessdump));
                commandRunTime = System.currentTimeMillis();
                Process p = processBuilder.directory(folder).start();
                if (wait) {
                    p.waitFor();
                    System.out.println(String.format("command was ran in %d milli seconds", (System.currentTimeMillis() - commandRunTime)));
                    return p;
                } else {
                    return p;
                }
            }
            else
            {
                System.out.println(String.format("running cmd on host: %s",sshManager.getStrConnectionIP()));
                sshManager.connect();
                if(executePath!=null)
                {
                    String cmd = String.format("cd %s && %s", executePath, command);
                    Process process = sshManager.sendCommand(cmd);
                    return process;
                }
                sshManager.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static void initOutPutProcessdump(){
        outPutProcessdump = new File(outPutfile);
        if(!outPutProcessdump.exists()){
            FileCommands.writeToFile(outPutfile,"Creation time of file is " +  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        }
    }
    public static void deleteOutPutProcessdump(){
        outPutProcessdump = new File(outPutfile);
        if(outPutProcessdump.exists()){
            outPutProcessdump.delete();
        }
    }

    public static String vendorType() {
        String[] cmd = {"/bin/sh", "-c", "rpm -qa | grep presidio-logpoint"};
        String output = FileCommands.executeCommand(cmd);
        if (output.equals(""))
            return "Quest";
        return "LogPoint";
    }
}