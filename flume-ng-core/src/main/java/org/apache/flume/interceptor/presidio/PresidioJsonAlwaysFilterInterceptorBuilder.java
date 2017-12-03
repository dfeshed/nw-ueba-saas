package org.apache.flume.interceptor.presidio;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;


/**
 * Builder which builds new instance of the JsonFilterInterceptor where the predicate always return true (always filter)
 */
public class PresidioJsonAlwaysFilterInterceptorBuilder extends AbstractPresidioJsonFilterInterceptorBuilder {


    @Override
    protected List<BiPredicate<JsonObject, String>> readPredicatesConfiguration(String[] predicatesArray, String predicatesParamsDelim, int numOfFields) {

        List<BiPredicate<JsonObject, String>> ans = new ArrayList<>();
        for (int i = 0; i < numOfFields; i++) {
            ans.add(createPredicate(fields.get(i), "", predicatesParamsDelim)); // it doesn't matter what string we input
        }

        return ans;
    }

    @Override
    protected BiPredicate<JsonObject, String> createPredicate(String string, String predicateString, String predicatesDelim) {
        return (jsonObject, s) -> true;
    }

}
