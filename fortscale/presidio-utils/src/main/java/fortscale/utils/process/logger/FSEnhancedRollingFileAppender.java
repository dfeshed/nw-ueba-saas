package fortscale.utils.process.logger;

import ch.qos.logback.core.rolling.RollingFileAppender;


public class FSEnhancedRollingFileAppender extends RollingFileAppender {

    private boolean isMultiProcess;

    /**
     *
     * @return is logger configed to run in multiProcess mode?
     */
    public boolean getIsMultiProcess() {
        return isMultiProcess;
    }

    /**
     * set isMultiProcess flag
     * @param multiProcess true: it is multiprocess false: otherwise
     */
    public void setIsMultiProcess(boolean multiProcess) {
        //filled by logback with reflection
        this.isMultiProcess = multiProcess;
    }


}
