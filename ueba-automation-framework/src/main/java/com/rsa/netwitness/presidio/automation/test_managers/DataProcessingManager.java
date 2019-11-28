package com.rsa.netwitness.presidio.automation.test_managers;

import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;

import java.time.Instant;
import java.util.Optional;

public class DataProcessingManager {

    public SshResponse stopAirflowScheduler() {
        return new SshHelper().uebaHostRootExec().run("systemctl stop airflow-scheduler");
    }

    public SshResponse startAirflowScheduler() {
        return new SshHelper().uebaHostRootExec().run("systemctl start airflow-scheduler");
    }

    public SshResponse saveCurrentTimeToFile(String fileName) {
        return new SshHelper().uebaHostRootExec().run("date --utc +%FT%T.%3NZ > /home/presidio/" + fileName);
    }

    public Optional<Instant> getInstantFromFile(String fileName) {
        SshResponse response = new SshHelper().uebaHostRootExec().run("cat /home/presidio/" + fileName);


        String ts = response.output.get(0).trim();

        try {
            return Optional.ofNullable(Instant.parse(ts));
        } catch (Exception e) {

        }

        return Optional.empty();
    }
}
