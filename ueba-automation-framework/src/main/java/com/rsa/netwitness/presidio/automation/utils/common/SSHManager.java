package com.rsa.netwitness.presidio.automation.utils.common;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.rsa.netwitness.presidio.automation.context.AutomationConf;
import org.slf4j.LoggerFactory;
import org.testng.collections.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

//import org.slf4j.LoggerFactory;


/**
 * SSH remote command executor
 * inspired by: {@link <a>https://stackoverflow.com/questions/2405885/run-a-command-over-ssh-with-jsch</a>}
 */
public class SSHManager {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SSHManager.class.getName());

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


    String connect() {
        String errorMessage = null;

        try {
            session = jschSSHChannel.getSession(userName, host, port);
            session.setPassword(password);
            // UNCOMMENT THIS FOR TESTING PURPOSES, BUT DO NOT USE IN PRODUCTION
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(timeOut);
        } catch (JSchException jschX) {
            errorMessage = jschX.getMessage();
        }

        return errorMessage;
    }


    public String getSshHost(){
        return host;
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

    public Response runCmd(String command) {
        return runCmd(command, false);
    }

    public Response runCmd(String command, boolean muteLogging) {

        try {
            connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();

            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(channel.getErrStream()));

            String line = null;
            List<String> errorRepose = Lists.newArrayList();
            while ((line = error.readLine()) != null) {
                if (!muteLogging) LOGGER.error(line);
                errorRepose.add(line);
            }

            line = null;
            List<String> inputRepose = Lists.newArrayList();
            while ((line = input.readLine()) != null) {
                if (!muteLogging) LOGGER.info(line);
                inputRepose.add(line);
            }

            input.close();
            error.close();
            return new Response(channel.getExitStatus(), inputRepose, errorRepose);

        } catch (IOException ioX) {
            LOGGER.error("Unable to start process");
            return null;
        } catch (JSchException jschX) {
            LOGGER.error("Unable to start process");
            return null;
        } finally {
            close();
        }
    }

    public void close() {
        session.disconnect();
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

