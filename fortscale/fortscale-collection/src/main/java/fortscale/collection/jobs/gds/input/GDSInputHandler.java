package fortscale.collection.jobs.gds.input;

import java.util.Set;

/**
 * Interface for generic data source input handler
 *
 * @author gils
 * 30/12/2015
 */
public interface GDSInputHandler {
    boolean getYesNoInput() throws Exception;
    String getInput() throws Exception;
    String getInput(Set<String> allowedValues) throws Exception;
    void close();
}
