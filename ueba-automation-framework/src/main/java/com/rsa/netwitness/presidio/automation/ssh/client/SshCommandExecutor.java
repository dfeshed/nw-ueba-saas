package com.rsa.netwitness.presidio.automation.ssh.client;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.LoggerFactory;
import org.testng.collections.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;


/**
 * The SshCommandExecutor uses the net.schmizz.sshj library to execute remote
 * commands.
 *
 * <li>Opens a session channel</li>
 * <li>Execute a command on the session</li>
 * <li>Closes the session</li>
 * <li>Disconnects</li>
 * </ol>
 **
 */
class SshCommandExecutor {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SshCommandExecutor.class.getName());
    private ServerDetails serverDetails;
    private static final String defaultUserDir= "/var/netwitness/presidio/batch/";

    SshCommandExecutor(ServerDetails serverDetails) {
        this.serverDetails = serverDetails;
    }

    SshResponse execute(String command, boolean verbose, String userDir) {
        Objects.requireNonNull(serverDetails, "serverDetails is not set.");
        final String finalUserDir = userDir.isEmpty() ? defaultUserDir : userDir;
        final String CMD = "cd ".concat(finalUserDir).concat(" ; ") + command;

        Session session = null;
        ChannelExec channel = null;
        try {

            session = StackSessionPool.getInstance().getPool().borrowObject(serverDetails);
            channel = (ChannelExec) session.openChannel("exec");
            System.out.println("\n>>> Run SSH command: [" + CMD + "]");
            channel.setCommand(CMD);
            channel.connect();

            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(channel.getErrStream()));

            String line = null;
            List<String> errorRepose = Lists.newArrayList();
            while ((line = error.readLine()) != null) {
                if (verbose) { LOGGER.error(line); }
                errorRepose.add(line);
            }

            line = null;
            List<String> inputRepose = Lists.newArrayList();
            while ((line = input.readLine()) != null) {
                if (verbose) { LOGGER.info(line); }
                inputRepose.add(line);
            }

            input.close();
            error.close();
            return new SshResponse(channel.getExitStatus(), inputRepose, errorRepose);


        }  catch (IOException | JSchException ioX) {
            LOGGER.error("Unable to start process.");
            LOGGER.error(serverDetails.toString());
            ioX.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != channel && channel.isConnected()) {
                channel.disconnect();
            }
            if (null != session) {
                try {
                    StackSessionPool.getInstance().getPool()
                            .returnObject(serverDetails, session);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}