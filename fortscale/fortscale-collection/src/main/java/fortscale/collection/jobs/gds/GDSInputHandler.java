package fortscale.collection.jobs.gds;

import java.util.Map;
import java.util.Set;

/**
 * @author gils
 * 30/12/2015
 */
public interface GDSInputHandler {
    String getInput() throws Exception;
    String getInput(String paramName) throws Exception;
    Map<String, String> getInput(Set<String> paramNames);
    void close();
}
