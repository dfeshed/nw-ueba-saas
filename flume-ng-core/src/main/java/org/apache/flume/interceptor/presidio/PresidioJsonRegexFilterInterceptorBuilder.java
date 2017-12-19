package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PresidioJsonRegexFilterInterceptorBuilder extends AbstractPresidioJsonFilterInterceptorBuilder {
    @Override
    protected BiPredicate<JsonObject, String> createPredicate(String fieldName, String predicateString, String predicatesParamsDelim) {
        final String[] split = predicateString.split(predicatesParamsDelim);
        Preconditions.checkArgument(split.length == 2, PREDICATES_CONF_NAME + " is invalid. One of the predicates doesn't have enough parameters. Needs 2 params, has " + split.length);
        return new BiPredicate<JsonObject, String>() {
            @Override
            public boolean test(JsonObject jsonObject, String currFieldValue) {
                String regex = split[1];
                final Pattern pattern = Pattern.compile(regex);
                final Matcher matcher = pattern.matcher(currFieldValue);

                return matcher.matches();
            }
        };
    }
}
