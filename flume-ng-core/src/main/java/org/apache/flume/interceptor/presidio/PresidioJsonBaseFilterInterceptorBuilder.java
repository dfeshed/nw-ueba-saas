package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import fortscale.utils.logging.Logger;

import java.util.function.Predicate;

public class PresidioJsonBaseFilterInterceptorBuilder extends AbstractPresidioJsonFilterInterceptorBuilder {

    private static final Logger logger = Logger.getLogger(PresidioJsonBaseFilterInterceptorBuilder.class);
    protected static final String FIELD_MARKER = "|field|";

    @Override
    protected Predicate<String> createPredicate(String predicateString, String predicatesParamsDelim) {
        final String[] split = predicateString.split(predicatesParamsDelim);
        Preconditions.checkArgument(split.length == 3, PREDICATES_CONF_NAME + " is invalid. One of the predicates doesn't have enough parameters. Needs 3 params, has " + split.length);
        return currFieldValue -> {
            final String[] split1 = currFieldValue.split(predicatesParamsDelim);
            final FilterOp op = FilterOp.createOp(split1[1]);
            final String toCompare = split1[2];
            return op.evaluate(currFieldValue, toCompare);
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
        CONTAINS {
            @Override
            public boolean evaluate(String currFieldValue, String rightSide) {
                return currFieldValue.contains(rightSide);
            }
        };

        public abstract boolean evaluate(String leftSide, String rightSide);


        public static FilterOp createOp(String opName) throws IllegalArgumentException {
            return FilterOp.valueOf(opName.toUpperCase().replace(" ", "_"));
        }
    }
}
