package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fortscale.utils.logging.Logger;

import java.util.function.BiPredicate;

import static org.apache.flume.interceptor.presidio.AbstractPresidioJsonInterceptor.EMPTY_STRING;

public class PresidioJsonBaseFilterInterceptorBuilder extends AbstractPresidioJsonFilterInterceptorBuilder {

    private static final Logger logger = Logger.getLogger(PresidioJsonBaseFilterInterceptorBuilder.class);
    protected static final String FIELD_MARKER = "|field|";

    @Override
    protected BiPredicate<JsonObject, String> createPredicate(String fieldName, String predicateString, String predicatesParamsDelim) {
        final String[] split = predicateString.split(predicatesParamsDelim);
        Preconditions.checkArgument(split.length == 2, PREDICATES_CONF_NAME + " is invalid. One of the predicates doesn't have enough parameters. Needs 2 params, has " + split.length);
        return new BiPredicate<JsonObject, String>() {
            @Override
            public boolean test(JsonObject jsonObject, String currFieldValue) {
                final FilterOp op = FilterOp.createOp(split[0]);
                String toCompareTo = split[1];
                if (toCompareTo.startsWith(FIELD_MARKER)) { //if we want to compare
                    final String toCompareFieldName = toCompareTo.substring(FIELD_MARKER.length());
                    JsonElement jsonElement = jsonObject.get(toCompareFieldName);
                    if (jsonElement == null || jsonElement.isJsonNull()) {
                        logger.debug("PresidioJsonBaseFilterInterceptorBuilder is comparing to an empty field %s", toCompareFieldName);
                        toCompareTo = "";
                    } else {
                        toCompareTo = jsonElement.getAsString();
                    }
                } else {
                    if (toCompareTo.equals(EMPTY_STRING)) {
                        toCompareTo = "";
                    }
                }
                return op.evaluate(currFieldValue, toCompareTo);
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
