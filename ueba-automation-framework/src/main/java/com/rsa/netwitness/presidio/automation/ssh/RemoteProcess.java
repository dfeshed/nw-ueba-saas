package com.rsa.netwitness.presidio.automation.ssh;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Process dummy that is puplated by {@link SSHManager}
 */
public class RemoteProcess extends Process {

    private OutputStream outputStream;
    private InputStream inputStream;
    private InputStream errorStream;
    private int exitValue;

    public RemoteProcess(OutputStream outputStream, InputStream inputStream, InputStream errorStream, int exitValue) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.errorStream = errorStream;
        this.exitValue = exitValue;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public InputStream getErrorStream() {
        return errorStream;
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }

    @Override
    public int exitValue() {
        return exitValue;
    }

    @Override
    public void destroy() {

    }
}
