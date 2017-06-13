package fortscale.services.parameters;


import fortscale.common.general.Command;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.utils.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import static fortscale.common.general.CommonStrings.COMMAND_LINE_DATE_FORMAT;
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
    public void validateDataSourceParam(String dataSource) throws Exception {
        DataSource.createDataSource(dataSource);
    }

    @Override
    public void validateTimeParams(String startDateAsString, String endDateAsString) throws Exception {
        Date startDate = new SimpleDateFormat(COMMAND_LINE_DATE_FORMAT).parse(startDateAsString);
        Date endDate = new SimpleDateFormat(COMMAND_LINE_DATE_FORMAT).parse(endDateAsString);

        if (!(startDate.before(endDate))) { //todo: maybe we can check that it's exactly 1 hour?
            throw new Exception(String.format("%s must be before than %s! %s:%s, %s:%s", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate));
        }
        Date now = new Date(System.currentTimeMillis());
        if (endDate.after(now)) {
            throw new Exception(String.format("%s can't be in the future! %s:%s, %s:%s", CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate, "now", now));
        }
    }

    @Override
    public void validateCommand(String command) throws Exception {
        Command.createCommand(command);
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

