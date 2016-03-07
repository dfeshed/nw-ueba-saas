package fortscale.collection.jobs.notifications;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

/**
 * factory class for the different hostnames manipulators.
 * mainly used by the notification jobs
 *
 * Created by galiar on 03/03/2016.
 */
public class HostnameManipulatorFactory {

    @Autowired
    DropDomainManipulator dropDomainManipulator;

    HostnameManipulator getHostnameManilpulator(String hostnameManipulatorString){

        if(hostnameManipulatorString.equals("DropDomainManipulator")){
            return dropDomainManipulator;
        }

        throw new UnsupportedOperationException( String.format("No such hostname manipulator exist : {}. aborting.. ",hostnameManipulatorString));
    }

}
