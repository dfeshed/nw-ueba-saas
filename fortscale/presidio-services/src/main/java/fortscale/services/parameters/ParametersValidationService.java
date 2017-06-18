package fortscale.services.parameters;


public interface ParametersValidationService {

    /**
     * gets mandatory parameter by {@param paramName}
     *
     * @param paramName
     * @param params
     * @return the extracted param value
     * @throws Exception if no such param was provided
     */
    String getMandatoryParamAsString(String paramName, String... params) throws Exception;

    /**
     * gets optional parameter by {@param paramName}
     *
     * @param paramName
     * @param params
     * @return the extracted param value or null if no such param was provided
     * @throws Exception if no such param exists
     */
    String getOptionalParamAsString(String paramName, String... params);

    /**
     * validates that the given {@param dataSource} is a valid dataSource name
     *
     * @param dataSource
     * @throws Exception that the given {@param dataSource} isn't a valid dataSource name
     */
    void validateDataSourceParam(String dataSource) throws Exception;

    /**
     * validates that the given {@param startTime} and {@param endTime} are valid.
     * Also validates logical validity of the pair (for example that {@param startTime}<{@param endTime})
     *
     * @param startTime
     * @param endTime
     * @throws Exception if the given {@param startTime} and {@param endTime} are not valid
     */
    void validateTimeParams(String startTime, String endTime) throws Exception;

    /**
     * validates that the given {@param command} is a valid command name
     *
     * @param command
     * @throws Exception that the given {@param command} isn't a valid command name
     */
    void validateCommand(String command) throws Exception;
}
