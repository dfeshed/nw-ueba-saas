package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractPresidioJsonFilterInterceptorBuilder extends AbstractPresidioInterceptorBuilder {

    private static final Logger logger = Logger.getLogger(AbstractPresidioJsonFilterInterceptorBuilder.class);

    protected static final String FIELDS_CONF_NAME = "fields";
    protected static final String DELIMITER_CONF_NAME = "delimiter";
    protected static final String PREDICATES_CONF_NAME = "predicates";
    protected static final String PREDICATES_PARAMS_DELIMITER_CONF_NAME = "predicatesParamsDelimiter"; //this is not the delimiter for the list of predicates! This is the delim for the parameters of a single predicate
    protected static final String DEFAULT_DELIMITER_VALUE = ",";
    protected static final String DEFAULT_PREDICATES_PRAMS_DELIMITER_VALUE = "#";
    protected static final String DEFAULT_PREDICATES_VALUE = "DEFAULT_PREDICATES_VALUE";

    protected List<String> fields;
    protected List<Predicate<String>> predicates;

    @Override
    public void doConfigure(Context context) {
        String delim = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);
        String predicatesParamsDelim = context.getString(PREDICATES_PARAMS_DELIMITER_CONF_NAME, DEFAULT_PREDICATES_PRAMS_DELIMITER_VALUE);

        String fieldsArrayAsString = context.getString(FIELDS_CONF_NAME);
        Preconditions.checkArgument(StringUtils.isNotEmpty(fieldsArrayAsString), FIELDS_CONF_NAME + " can not be empty.");

        final String[] fieldsArray = fieldsArrayAsString.split(delim);
        String currField;
        fields = new ArrayList<>();
        for (int i = 0; i < fieldsArray.length; i++) {
            currField = fieldsArray[i];
            Preconditions.checkArgument(StringUtils.isNotEmpty(currField), "%s(index=%s) can not be empty. %s=%s.", FIELDS_CONF_NAME, i, FIELDS_CONF_NAME, fieldsArrayAsString);
            fields.add(currField);
        }

        String predicatesArrayAsString = context.getString(PREDICATES_CONF_NAME, DEFAULT_PREDICATES_VALUE);
        Preconditions.checkArgument(StringUtils.isNotEmpty(predicatesArrayAsString), PREDICATES_CONF_NAME + " can not be empty.");
        final String[] predicatesArray = predicatesArrayAsString.split(delim);
        predicates = readPredicatesConfiguration(predicatesArray, predicatesParamsDelim, fields.size());


    }

    protected List<Predicate<String>> readPredicatesConfiguration(String[] predicatesArray, String predicatesParamsDelim, int numOfFields) {
        List<Predicate<String>> ans = new ArrayList<>();
        Predicate<String> currPredicate;
        for (int i = 0; i < predicatesArray.length; i++) {
            final String predicateString = predicatesArray[i];
            Preconditions.checkArgument(StringUtils.isNotEmpty(predicateString), "%s(index=%s) can not be empty. %s=%s.", PREDICATES_CONF_NAME, i, PREDICATES_CONF_NAME, predicatesArray);
            currPredicate = createPredicate(predicateString, predicatesParamsDelim);
            ans.add(currPredicate);
        }
        Preconditions.checkArgument(numOfFields == predicates.size(), "The configurations: %s and %s must have the same number of elements", FIELDS_CONF_NAME, PREDICATES_CONF_NAME);
        return ans;
    }


    protected abstract Predicate<String> createPredicate(String predicateString, String predicatesParamsDelim);

    @Override
    public AbstractPresidioJsonInterceptor doBuild() {
        final JsonFilterInterceptor jsonFilterInterceptor = new JsonFilterInterceptor(fields, predicates);
        logger.info("Creating JsonFilterInterceptor: {}", jsonFilterInterceptor);
        return jsonFilterInterceptor;
    }
}
