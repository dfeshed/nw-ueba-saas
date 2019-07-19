package com.rsa.netwitness.presidio.automation.utils.common;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//import org.slf4j.LoggerFactory;


/**
 * SSH remote command executor
 * inspired by: {@link <a>https://stackoverflow.com/questions/2405885/run-a-command-over-ssh-with-jsch</a>}
 */
public class SSHManager
{
//    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SSHManager.class.getName());

    private JSch jschSSHChannel;
    private String strUserName;
    private String strConnectionIP;
    private int intConnectionPort;
    private String strPassword;
    private Session sesConnection;
    private int intTimeOut;

    private void doCommonConstructorActions(String userName,
                                            String password, String connectionIP, String knownHostsFileName)
    {
        jschSSHChannel = new JSch();

        try
        {
            jschSSHChannel.setKnownHosts(knownHostsFileName);
        }
        catch(JSchException jschX)
        {
            logError(jschX.getMessage());
        }

        strUserName = userName;
        strPassword = password;
        strConnectionIP = connectionIP;
    }

    public SSHManager(String userName, String password,
                      String connectionIP, String knownHostsFileName)
    {
        doCommonConstructorActions(userName, password,
                connectionIP, knownHostsFileName);
        intConnectionPort = 22;
        intTimeOut = 60000;
    }

    public SSHManager() throws IOException {
        Properties prop = new Properties();
        prop.load(SSHManager.class.getClassLoader().getResourceAsStream("sshmanager.properties"));
        String userName = prop.getProperty("ssh.userName");
        String password = prop.getProperty("ssh.password");
        String ip = prop.getProperty("ssh.connectionIP");
        String knownHostsFileName = prop.getProperty("ssh.knownHostsFileName");
        doCommonConstructorActions(userName,password,ip,knownHostsFileName);
        intConnectionPort = 22;
        intTimeOut = 60000;
    }

    public SSHManager(String userName, String password, String connectionIP,
                      String knownHostsFileName, int connectionPort)
    {
        doCommonConstructorActions(userName, password, connectionIP,
                knownHostsFileName);
        intConnectionPort = connectionPort;
        intTimeOut = 60000;
    }

    public SSHManager(String userName, String password, String connectionIP,
                      String knownHostsFileName, int connectionPort, int timeOutMilliseconds)
    {
        doCommonConstructorActions(userName, password, connectionIP,
                knownHostsFileName);
        intConnectionPort = connectionPort;
        intTimeOut = timeOutMilliseconds;
    }

    public String connect()
    {
        String errorMessage = null;

        try
        {
            sesConnection = jschSSHChannel.getSession(strUserName,
                    strConnectionIP, intConnectionPort);
            sesConnection.setPassword(strPassword);
            // UNCOMMENT THIS FOR TESTING PURPOSES, BUT DO NOT USE IN PRODUCTION
             sesConnection.setConfig("StrictHostKeyChecking", "no");
            sesConnection.connect(intTimeOut);
        }
        catch(JSchException jschX)
        {
            errorMessage = jschX.getMessage();
        }

        return errorMessage;
    }

    private String logError(String errorMessage)
    {
        if(errorMessage != null)
        {
            System.out.println(String.format("ERROR %s:%d - %s",
                    strConnectionIP, intConnectionPort, errorMessage));
        }

        return errorMessage;
    }

    private String logWarning(String warnMessage)
    {
        if(warnMessage != null)
        {
            System.out.println(String.format("WARNING %s:%d - %s",
                    strConnectionIP, intConnectionPort, warnMessage));
        }

        return warnMessage;
    }

    public Process sendCommand(String command) {
        StringBuilder outputBuffer = new StringBuilder();

        try {
            ChannelExec channel = (ChannelExec) sesConnection.openChannel("exec");
            channel.setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            int readByte = commandOutput.read();

            while (readByte != 0xffffffff) {
                outputBuffer.append((char) readByte);
                readByte = commandOutput.read();
            }

            channel.disconnect();
            System.out.println(outputBuffer.toString());
            Process process = new RemoteProcess(channel.getOutputStream(), channel.getInputStream(),
                    channel.getErrStream(), channel.getExitStatus());
            return process;

        } catch (IOException ioX) {
            logWarning(ioX.getMessage());
            return null;
        } catch (JSchException jschX) {
            logWarning(jschX.getMessage());
            return null;
        }
    }

    public void close()
    {
        sesConnection.disconnect();
    }

    public String getStrUserName() {
        return strUserName;
    }

    public String getStrConnectionIP() {
        return strConnectionIP;
    }

    public int getIntConnectionPort() {
        return intConnectionPort;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public Session getSesConnection() {
        return sesConnection;
    }

    public int getIntTimeOut() {
        return intTimeOut;
    }
}

