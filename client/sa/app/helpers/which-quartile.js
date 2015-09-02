/**
 * @file Quartile computer helper.
 * Converts a number (0 to 100) to a quartile (0 thru 3).
 */
import Ember from "ember";

export function whichQuartile(params/*, hash*/) {
    var num = parseInt(params[0], 10) || 0;
    return Math.max(0, Math.min(3, parseInt(num / 25, 10)));
}

export default Ember.Helper.helper(whichQuartile);
