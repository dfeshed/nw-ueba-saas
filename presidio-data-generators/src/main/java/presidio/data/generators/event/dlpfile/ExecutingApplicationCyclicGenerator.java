package presidio.data.generators.event.dlpfile;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.dlpfileop.DEFAULT_APPLICATIONS;

/**
 * Created by cloudera on 6/1/17.
 * This class is one element data provider from a cyclic list of string values
 */
public class ExecutingApplicationCyclicGenerator extends AbstractCyclicValuesGenerator {
    private static final String[] DEFAULT_EXECUTING_APPLICATION_ARRAY = {DEFAULT_APPLICATIONS.POWERSHELL_EXE.value,DEFAULT_APPLICATIONS.POWERCFG_EXE.value,DEFAULT_APPLICATIONS.EVENTVWR_EXE.value,
            DEFAULT_APPLICATIONS.COMPMGMT_EXE.value,DEFAULT_APPLICATIONS.TASKSCHD_EXE.value,DEFAULT_APPLICATIONS.SECPOL_EXE.value};


    public ExecutingApplicationCyclicGenerator() {
        super(DEFAULT_EXECUTING_APPLICATION_ARRAY);
    }

    public ExecutingApplicationCyclicGenerator(String[] customList) {
        super(customList);
    }
}
