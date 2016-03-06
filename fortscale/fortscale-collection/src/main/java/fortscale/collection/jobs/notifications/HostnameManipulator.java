package fortscale.collection.jobs.notifications;

import java.util.List;

/**
 * used to manipulate hostnames.
 * used mainly in notification creation.
 *
 * Created by galiar on 03/03/2016.
 */
public interface HostnameManipulator {

    String getManipulatedHostname(String hostname, List<String> additionalInput);
}
