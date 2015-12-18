/**
 * @file Date object formatting
 * Uses moment javascript library to do formatting.
 * @public
 */
/* global moment */
import Ember from 'ember';

export function formatMoment(params/*, hash*/) {
  return params[0] ?
      moment(Number(params[0])).format(params[1]) : '';
}

export default Ember.Helper.helper(formatMoment);
