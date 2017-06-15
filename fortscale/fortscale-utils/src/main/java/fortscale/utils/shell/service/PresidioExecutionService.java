package fortscale.utils.shell.service;

/**
 * Created by efratn on 12/06/2017.
 */
public interface PresidioExecutionService {
    //TODO change dates from string to Date obj
    public void process(String dataSource, String startTime, String endTime) throws Exception;

    public void clean(String dataSource, String startTime, String endTime);
}
