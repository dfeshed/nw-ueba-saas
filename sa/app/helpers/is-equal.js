/**
 * @file Generic comparison helper.
 * Compares two values and returns a boolean.
 * @public
 */
import Ember from 'ember';

const { Helper: { helper } } = Ember;

export function isEqual(params/*, hash*/) {
  let [leftSide, rightSide, notStrict] = params;

  /*jshint eqeqeq:false */
  return notStrict ?  (leftSide == rightSide) : (leftSide === rightSide);
}

export default helper(isEqual);
