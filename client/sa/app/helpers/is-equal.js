/**
 * @file Generic comparison helper.
 * Compares two values and returns a boolean.
 */
import Ember from "ember";

export function isEqual(params/*, hash*/) {
    var leftSide = params[0],
        rightSide = params[1],
        notStrict = params[2];
    /*jshint eqeqeq:false */
    return notStrict ?  (leftSide == rightSide) : (leftSide === rightSide);
}

export default Ember.Helper.helper(isEqual);
