package com.rsa.netwitness.presidio.automation.ssh.client;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.rsa.netwitness.presidio.automation.ssh.helper.ServerDetails;
import org.elasticsearch.common.collect.EvictingQueue;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Queue;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


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
public class SshCommandExecutor {
    static Logger LOGGER = (Logger) LoggerFactory.getLogger(SshCommandExecutor.class);


    private static final int MAX_LINES_TO_COLLECT = 400;
    private final ServerDetails serverDetails;
    private final String command;
    private final String userDir;
    private final boolean verbose;

    SshCommandExecutor(ServerDetails serverDetails, String command, String userDir, boolean verbose) {
        this.serverDetails = serverDetails;
        this.command = command;
        this.userDir = userDir;
        this.verbose = verbose;
    }

    SshResponse execute() {

        Objects.requireNonNull(serverDetails, "serverDetails not set.");
        final String CMD = "cd ".concat(userDir).concat(" ; ") + command;

        Session session = null;
        ChannelExec channel = null;
        PipedOutputStream outputStream = null;
        PipedInputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            LOGGER.info(serverDetails.user + "@" + serverDetails.host + ": [" + CMD + "]");

            session = StackSessionPool.getInstance().getPool().borrowObject(serverDetails);
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(CMD);
            channel.setPtyType("dumb");

            outputStream = new PipedOutputStream();
            channel.setOutputStream(outputStream);
            channel.setErrStream(outputStream);

            inputStream = new PipedInputStream(outputStream);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, US_ASCII));
            Instant startTime = Instant.now();
            channel.connect();
            LOGGER.debug("Connection time: " + Duration.between(Instant.now(), startTime).toMillis() + " ms");

            waitReady(bufferedReader);

            String line;
            Queue<String> sshOutput = new EvictingQueue<>(MAX_LINES_TO_COLLECT);
            while ((line = bufferedReader.readLine()) != null || channel.getExitStatus()!=0) {
                if (verbose) LOGGER.info(line);
                sshOutput.add(line);
                waitReady(bufferedReader);
            }

            if (channel.getExitStatus()!=0) {
                LOGGER.warn("Non zero exit code return [" + channel.getExitStatus() + "]");
                printOut(sshOutput);
            }

            LOGGER.info("Finished in " + Duration.between(startTime, Instant.now()).toMillis() + " ms");
            return new SshResponse(channel.getExitStatus(), Lists.newLinkedList(sshOutput));


        } catch (IOException | JSchException ioX) {
            LOGGER.error("Run ssh cmd failed.\nDetails: " + serverDetails.toString());
            ioX.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (null != session) {
                    StackSessionPool.getInstance().getPool().returnObject(serverDetails, session);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (null != channel && channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    private void waitReady(BufferedReader br) throws IOException, InterruptedException {
        int i=0;
        LOGGER.info("Waiting until the buffer is ready.");
        while (!br.ready() && i < 10) {
            MILLISECONDS.sleep(10);
            i++;
        }
        if (br.ready()) {
            LOGGER.info("Ready");
        } else {
            LOGGER.info("Timeout");
        }
    }

    private void printOut(Queue<String> sshOutput) {
        LOGGER.warn("CMD output:");
        LOGGER.warn("***********************************************");
        sshOutput.forEach(System.out::println);
        LOGGER.warn("***********************************************");

    }
}