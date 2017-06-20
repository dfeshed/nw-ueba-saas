package fortscale.monitoring.processes.group;

import fortscale.utils.process.standardProcess.StandardProcessBase;


public abstract class MonitoringProcessGroupCommon extends StandardProcessBase {

    @Override
    protected String getProcessGroupName()
    {
        return "monitoring";
    }

}
