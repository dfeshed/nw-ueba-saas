package com.rsa.netwitness.presidio.automation.ssh.helper;

import com.rsa.netwitness.presidio.automation.ssh.client.SshExecHelper;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import java.util.function.Supplier;
import static java.util.concurrent.TimeUnit.SECONDS;

public class SshHelper {
    private static Lazy<SshResponse> presidioUnlockTry = new Lazy<>();

    public SshExecHelper uebaHostExec() {
        presidioUnlockTry.getOrCompute(runUnlockPresidio);
        return new UebaHostSshExecutor();
    }

    public SshExecHelper uebaHostRootExec() {
        return new UebaHostSshExecutorRoot();
    }

    private Supplier<SshResponse> runUnlockPresidio = () ->
        new UebaHostSshExecutorRoot().setVerboseTrue().withTimeout(10, SECONDS)
                .run("faillock --user presidio --reset");
}
