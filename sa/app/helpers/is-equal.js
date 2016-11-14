/**
 * @file Generic comparison helper.
 * Compares two values and returns a boolean.
 * @public
 */
import Ember from 'ember';

const { Helper: { helper } } = Ember;

export function isEqual(params /* , hash */) {
  const [leftSide, rightSide, notStrict] = params;
  return notStrict ? (leftSide == rightSide) : (leftSide === rightSide);
}

export default helper(isEqual);
