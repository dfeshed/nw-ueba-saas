package fortscale.services.dataqueries.querydto;

/**
* Created by Yossi on 10/11/2014.
*/
public enum QueryOperator {
    equals, notEquals,
    greaterThan, greaterThanOrEquals,
    lesserThan, lesserThanOrEquals,
    in,
    between,
    like,
    startsWith, endsWith, contains,
    hasValue,
    hasNoValue,
    stringHasValue,
    stringHasNoValue,
    regex
}
