package org.apache.flume.interceptor.presidio.transform.predicate;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.flume.interceptor.presidio.transform.predicate.JsonObjectChainPredicate.LogicalOperation.AND;
import static org.apache.flume.interceptor.presidio.transform.predicate.JsonObjectChainPredicate.LogicalOperation.OR;

public class JsonObjectChainPredicateTest {

    private void fillRegexPredicates(List<IJsonObjectPredicate> predicateList, String sourceKeyPrefix, int numberOfPredicates, JSONObject jsonObject, boolean testReturnValue){
        for(int i = 0; i < numberOfPredicates; i++){
            String sourceKey = sourceKeyPrefix + i;
            JsonObjectRegexPredicate predicate = new JsonObjectRegexPredicate("test-name", sourceKey, "^condition.*");

            if(testReturnValue) {
                jsonObject.put(sourceKey, "condition-testing");
            } else{
                jsonObject.put(sourceKey, "testing-condition");
            }
            predicateList.add(predicate);
        }
    }

    @Test
    public void single_predicate_or_op_with_match_test(){
        JSONObject jsonObject = new JSONObject();
        List<IJsonObjectPredicate> predicateList = new ArrayList<>();
        fillRegexPredicates(predicateList, "test-key", 1, jsonObject, true);

        JsonObjectChainPredicate jsonObjectChainPredicate = new JsonObjectChainPredicate("test-chain-pred",
                OR, predicateList);

        Assert.assertTrue("the predicate returned true hence the chain predicated should also return true", jsonObjectChainPredicate.test(jsonObject));
    }

    @Test
    public void multi_predicate_or_op_with_one_match_test(){
        JSONObject jsonObject = new JSONObject();
        List<IJsonObjectPredicate> predicateList = new ArrayList<>();
        fillRegexPredicates(predicateList, "failed-key1",3, jsonObject, false);
        fillRegexPredicates(predicateList, "success-key",1, jsonObject, true);
        fillRegexPredicates(predicateList, "failed-key2",3, jsonObject, false);
        JsonObjectChainPredicate jsonObjectChainPredicate = new JsonObjectChainPredicate("test-chain-pred",
                OR, predicateList);

        Assert.assertTrue("there is a predicate which returned true hence the chain predicate should also return true", jsonObjectChainPredicate.test(jsonObject));
    }

    @Test
    public void single_predicate_or_op_with_no_match_test(){
        JSONObject jsonObject = new JSONObject();
        List<IJsonObjectPredicate> predicateList = new ArrayList<>();
        fillRegexPredicates(predicateList, "test-key", 1, jsonObject, false);
        JsonObjectChainPredicate jsonObjectChainPredicate = new JsonObjectChainPredicate("test-chain-pred",
                OR, predicateList);

        Assert.assertFalse("there is only one predicate and it returned false hence the chain predicate should also return false", jsonObjectChainPredicate.test(jsonObject));
    }

    @Test
    public void multi_predicate_or_op_with_no_match_test(){
        JSONObject jsonObject = new JSONObject();
        List<IJsonObjectPredicate> predicateList = new ArrayList<>();
        fillRegexPredicates(predicateList, "failed-key1",6, jsonObject, false);
        JsonObjectChainPredicate jsonObjectChainPredicate = new JsonObjectChainPredicate("test-chain-pred",
                OR, predicateList);

        Assert.assertFalse("all predicates return false hence the chain predicate should also return false", jsonObjectChainPredicate.test(jsonObject));
    }

    @Test
    public void single_predicate_and_op_with_match_test(){
        JSONObject jsonObject = new JSONObject();
        List<IJsonObjectPredicate> predicateList = new ArrayList<>();
        fillRegexPredicates(predicateList, "test-key", 1, jsonObject, true);
        JsonObjectChainPredicate jsonObjectChainPredicate = new JsonObjectChainPredicate("test-chain-pred",
                AND, predicateList);

        Assert.assertTrue("there is only one predicate and it returned true hence the chain predicate should also return true", jsonObjectChainPredicate.test(jsonObject));
    }

    @Test
    public void multi_predicate_and_op_with_all_match_test(){
        JSONObject jsonObject = new JSONObject();
        List<IJsonObjectPredicate> predicateList = new ArrayList<>();
        fillRegexPredicates(predicateList, "failed-key1",6, jsonObject, true);
        JsonObjectChainPredicate jsonObjectChainPredicate = new JsonObjectChainPredicate("test-chain-pred",
                AND, predicateList);

        Assert.assertTrue("all predicates return true hence the chain predicate should also return true", jsonObjectChainPredicate.test(jsonObject));
    }

    @Test
    public void multi_predicate_and_op_with_one_unmatched_test(){
        JSONObject jsonObject = new JSONObject();
        List<IJsonObjectPredicate> predicateList = new ArrayList<>();
        fillRegexPredicates(predicateList, "failed-key1",3, jsonObject, true);
        fillRegexPredicates(predicateList, "success-key",1, jsonObject, false);
        fillRegexPredicates(predicateList, "failed-key2",3, jsonObject, true);
        JsonObjectChainPredicate jsonObjectChainPredicate = new JsonObjectChainPredicate("test-chain-pred",
                AND, predicateList);

        Assert.assertFalse("there is a predicate which returned false hence the chain predicate should also return false", jsonObjectChainPredicate.test(jsonObject));
    }

    @Test
    public void single_predicate_and_op_with_no_match_test(){
        JSONObject jsonObject = new JSONObject();
        List<IJsonObjectPredicate> predicateList = new ArrayList<>();
        fillRegexPredicates(predicateList, "test-key", 1, jsonObject, false);
        JsonObjectChainPredicate jsonObjectChainPredicate = new JsonObjectChainPredicate("test-chain-pred",
                AND, predicateList);

        Assert.assertFalse("the predicate returned false hence the chain predicate should also return false", jsonObjectChainPredicate.test(jsonObject));
    }

    private void assertCombinationOfOrPlusAndOperation(boolean orTestReturnValue, boolean andTestReturnValue,
                                                       JsonObjectChainPredicate.LogicalOperation operation, boolean expectedTestResult){
        JSONObject jsonObject = new JSONObject();
        List<IJsonObjectPredicate> predicateList1 = new ArrayList<>();
        fillRegexPredicates(predicateList1, "and-key",3, jsonObject, andTestReturnValue);
        JsonObjectChainPredicate jsonObjectChainPredicate1 = new JsonObjectChainPredicate("test-and-chain-pred",
                AND, predicateList1);
        List<IJsonObjectPredicate> predicateList2 = new ArrayList<>();
        fillRegexPredicates(predicateList2, "or-key",6, jsonObject, orTestReturnValue);
        JsonObjectChainPredicate jsonObjectChainPredicate2 = new JsonObjectChainPredicate("test-or-chain-pred",
                OR, predicateList2);

        JsonObjectChainPredicate jsonObjectChainPredicate = new JsonObjectChainPredicate("test-chain-combine", operation,
                Arrays.asList(jsonObjectChainPredicate1, jsonObjectChainPredicate2));
        Assert.assertTrue("the combination of predicates did not output the expected test result",
                jsonObjectChainPredicate.test(jsonObject) == expectedTestResult);
    }

    @Test
    public void combination_of_or_plus_and_operation_test1(){
        assertCombinationOfOrPlusAndOperation(false,false,AND, false);
    }

    @Test
    public void combination_of_or_plus_and_operation_test2(){
        assertCombinationOfOrPlusAndOperation(false,false,OR, false);
    }

    @Test
    public void combination_of_or_plus_and_operation_test3(){
        assertCombinationOfOrPlusAndOperation(false,true,AND, false);
    }

    @Test
    public void combination_of_or_plus_and_operation_test4(){
        assertCombinationOfOrPlusAndOperation(true,false,AND, false);
    }

    @Test
    public void combination_of_or_plus_and_operation_test5(){
        assertCombinationOfOrPlusAndOperation(true,true,AND, true);
    }

    @Test
    public void combination_of_or_plus_and_operation_test6(){
        assertCombinationOfOrPlusAndOperation(false,true,OR, true);
    }

    @Test
    public void combination_of_or_plus_and_operation_test7(){
        assertCombinationOfOrPlusAndOperation(true,false,OR, true);
    }

    @Test
    public void combination_of_or_plus_and_operation_test8(){
        assertCombinationOfOrPlusAndOperation(true,true,OR, true);
    }
}
