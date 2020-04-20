/**
 * @file Generic comparison helper.
 * Compares two values and returns a boolean.
 * @public
 */
import { helper } from '@ember/component/helper';

export function isEqual(params /* , hash */) {
  const [leftSide, rightSide, notStrict] = params;
  return notStrict ? (leftSide == rightSide) : (leftSide === rightSide);
}

export default helper(isEqual);
