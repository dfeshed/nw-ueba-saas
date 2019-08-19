package com.rsa.netwitness.presidio.automation.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.rsa.netwitness.presidio.automation.context.AutomationConf;
import org.slf4j.LoggerFactory;
import org.testng.collections.Lists;

import java.io.BufferedReader;
import java.io.IOException;
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


    SSHManager() {
        userName = AutomationConf.SSH_USERNAME;
        password = AutomationConf.SSH_PASSWORD;
        host =  AutomationConf.UEBA_HOST;
        knownHostsFileName = "";
        port = 22;
        timeOut = 60000;
        jschSSHChannel = new JSch();
    }


    String connect() {
        String errorMessage = null;

        try {
            jschSSHChannel.setKnownHosts(knownHostsFileName);
            session = jschSSHChannel.getSession(userName, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(timeOut);
        } catch (JSchException jschX) {
            errorMessage = jschX.getMessage();
        }

        return errorMessage;
    }


    public Response runCmd(String command) {
        return runCmd(command, false);
    }

    public Response runCmd(String command, boolean muteLogging) {

        try {
            connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            System.out.println("\n>>> Run SSH command: [" + command + "]");
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

        } catch (IOException | JSchException ioX) {
            LOGGER.error("Unable to start process.");
            LOGGER.error(this.toString());
            ioX.printStackTrace();
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


    @Override
    public String toString() {
        return "SSHManager{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", knownHostsFileName='" + knownHostsFileName + '\'' +
                ", timeOut=" + timeOut +
                '}';
    }

}

