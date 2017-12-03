package org.apache.flume.interceptor.presidio;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


/**
 * Builder which builds new instance of the JsonFilterInterceptor where the predicate always return true (always filter)
 */
public class PresidioJsonAlwaysFilterInterceptorBuilder extends AbstractPresidioJsonFilterInterceptorBuilder {


    @Override
    protected List<Predicate<String>> readPredicatesConfiguration(String[] predicatesArray, int numOfFields) {

        List<Predicate<String>> ans = new ArrayList<>();
        for (int i = 0; i < numOfFields; i++) {
            ans.add(createPredicate("")); // it doesn't matter what string we input
        }

        return ans;
    }

    @Override
    protected Predicate<String> createPredicate(String predicateString) {
        return s -> true;
    }

}
