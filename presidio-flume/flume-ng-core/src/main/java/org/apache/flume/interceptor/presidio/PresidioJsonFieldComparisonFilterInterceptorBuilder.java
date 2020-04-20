package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fortscale.utils.logging.Logger;

import java.util.function.BiPredicate;

public class PresidioJsonFieldComparisonFilterInterceptorBuilder extends AbstractPresidioJsonFilterInterceptorBuilder {

    private static final Logger logger = Logger.getLogger(PresidioJsonFieldComparisonFilterInterceptorBuilder.class);

    @Override
    protected BiPredicate<JsonObject, String> createPredicate(String fieldName, String predicateString, String predicatesParamsDelim) {
        final String[] split = predicateString.split(predicatesParamsDelim);
        Preconditions.checkArgument(split.length == 2, PREDICATES_CONF_NAME + " is invalid. One of the predicates doesn't have enough parameters. Needs 2 params, has " + split.length);
        return new BiPredicate<JsonObject, String>() {
            @Override
            public boolean test(JsonObject jsonObject, String currFieldValue) {
                final FilterOp op = FilterOp.createOp(split[0]);
                String fieldToCompareTo = split[1];
                JsonElement jsonElement = jsonObject.get(fieldToCompareTo);
                if (jsonElement == null || jsonElement.isJsonNull()) {
                    logger.debug("PresidioJsonFieldComparisonFilterInterceptorBuilder is comparing to an empty field %s", fieldToCompareTo);
                    fieldToCompareTo = "";
                } else {
                    fieldToCompareTo = jsonElement.getAsString();
                }

                return op.evaluate(currFieldValue, fieldToCompareTo);
            }
        };
    }

    private enum FilterOp {

        EQUALS {
            @Override
            public boolean evaluate(String currFieldValue, String rightSide) {
                return currFieldValue.equals(rightSide);
            }
        },
        NOT_EQUALS {
            @Override
            public boolean evaluate(String currFieldValue, String rightSide) {
                return !EQUALS.evaluate(currFieldValue, rightSide);
            }
        },
        EQUALS_IGNORE_CASE {
            @Override
            public boolean evaluate(String currFieldValue, String rightSide) {
                return currFieldValue.equalsIgnoreCase(rightSide);
            }
        },
        CONTAINS {
            @Override
            public boolean evaluate(String currFieldValue, String rightSide) {
                return currFieldValue.contains(rightSide);
            }
        },
        STARTS_WITH {
            @Override
            public boolean evaluate(String currFieldValue, String rightSide) {
                return currFieldValue.startsWith(rightSide);
            }
        },
        ENDS_WITH {
            @Override
            public boolean evaluate(String currFieldValue, String rightSide) {
                return currFieldValue.endsWith(rightSide);
            }
        };

        public abstract boolean evaluate(String leftSide, String rightSide);


        public static FilterOp createOp(String opName) throws IllegalArgumentException {
            return FilterOp.valueOf(opName.toUpperCase().replace(" ", "_"));
        }
    }
}
