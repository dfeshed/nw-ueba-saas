package fortscale.services.parameters;


import fortscale.common.general.CommonStrings;
import fortscale.common.general.Datasource;
import fortscale.utils.logging.Logger;

import static fortscale.common.general.CommonStrings.COMMAND_LINE_PARAM_DELIMITER;

public class ParametersServiceImpl implements ParametersValidationService {

    private static final Logger logger = Logger.getLogger(ParametersServiceImpl.class);

    @Override
    public String getMandatoryParamAsString(String paramName, String... params) throws Exception {
        final String param = getParamByName(paramName, params);
        if (param == null) {
            final String errorMessage = String.format("Mandatory param %s was not provided.", paramName);
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        } else {
            return param;
        }

    }

    @Override
    public String getOptionalParamAsString(String paramName, String... params) {
        final String param = getParamByName(paramName, params);
        if (param == null) {
            logger.info("Optional param {} was not provided.", paramName);
        }

        return param;
    }

    @Override
    public void validateDatasourceParam(String datasource) throws Exception {
        Datasource.createDataSource(datasource); //
    }

    @Override
    public void validateTimeParams(String startTimeAsString, String endTimeAsString) throws Exception {
        final long startTime = Long.parseLong(startTimeAsString);
        final long endTime = Long.parseLong(endTimeAsString);
        if (!(startTime >= 0)) {
            throw new Exception(String.format("%s can't be negative! %s:%s", CommonStrings.COMMAND_LINE_START_TIME_FIELD_NAME, CommonStrings.COMMAND_LINE_START_TIME_FIELD_NAME, startTime));
        }
        if (!(startTime < endTime)) { //todo: maybe we can check that it's exactly 1 hour?
            throw new Exception(String.format("%s must be less than %s! %s:%s, %s:%s", CommonStrings.COMMAND_LINE_START_TIME_FIELD_NAME, CommonStrings.COMMAND_LINE_END_TIME_FIELD_NAME, CommonStrings.COMMAND_LINE_START_TIME_FIELD_NAME, startTime, CommonStrings.COMMAND_LINE_END_TIME_FIELD_NAME, endTime));
        }
        final long now = System.currentTimeMillis();
        if (!(endTime <= now)) {
            throw new Exception(String.format("%s can't be in the future! %s:%s, %s:%s", CommonStrings.COMMAND_LINE_END_TIME_FIELD_NAME, CommonStrings.COMMAND_LINE_END_TIME_FIELD_NAME, endTime, "now", now));
        }
    }

    private String getParamByName(String paramName, String[] params) {
        for (String param : params) {
            final String[] splitParam = param.split(COMMAND_LINE_PARAM_DELIMITER);
            if (splitParam[0].toLowerCase().equals(paramName)) {
                return splitParam[1];
            }
        }
        return null;
    }


}

