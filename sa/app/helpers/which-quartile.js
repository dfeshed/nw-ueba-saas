/**
 * @file Quartile computer helper.
 * Converts a number (0 to 100) to a quartile (0 thru 3).
 * @public
 */
import Ember from 'ember';

const { Helper: { helper } } = Ember;

export function whichQuartile(params/* , hash */) {
  const num = parseInt(params[0], 10) || 0;
  return Math.max(0, Math.min(3, parseInt(num / 25, 10)));
}

export default helper(whichQuartile);
