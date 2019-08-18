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
    private Session session;


    private String userName;
    private String password;
    private String host;
    private int port;
    private String knownHostsFileName;
    private int timeOut;


    SSHManager() throws JSchException {
        userName = AutomationConf.SSH_USERNAME;
        password = AutomationConf.SSH_PASSWORD;
        host =  AutomationConf.UEBA_HOST;
        knownHostsFileName = "";
        port = 22;
        timeOut = 10000;
        jschSSHChannel = new JSch();
        jschSSHChannel.setKnownHosts(knownHostsFileName);
    }


    public String connect()
    {
        String errorMessage = null;

        try {
            session = jschSSHChannel.getSession(userName, host, port);
            session.setPassword(password);
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
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
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
            LOGGER.error("Unable to start process");
            ioX.getMessage();
            return null;
        } catch (JSchException jschX) {
            LOGGER.error("Unable to start process");
            jschX.getMessage();
            return null;
        }
    }

    public void close()
    {
        sesConnection.disconnect();
    }

    public class Response {
        public final int exitCode;
        public final List<String> output;
        public final List<String> error;

        private Response(int exitCode, List<String> output, List<String> error) {
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
        }

    }
}

