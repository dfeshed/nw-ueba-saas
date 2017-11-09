import Ember from 'ember';
import { uriEncodeEventQuery } from 'investigate-events/actions/helpers/query-utils';

const {
  get,
  getProperties,
  set,
  Helper
} = Ember;

/**
 * Composes a URI component string for a given query definition, plus (optional) drill path.
 * Useful for creating hyperlinks for drilling to the results of a NetWitness Core query for Events.
 * @param {object} queryAttrs
 * @param {string} [drillKey] Optional meta key to be appended to the given query.
 * @param {*} [drillValue] Optional meta key value to be appended to the given query.
 * @returns {*}
 * @public
 */
export function eventQueryUri([ queryAttrs, drillKey, drillOperator, drillValue ] /* , hash */) {
  if (!queryAttrs) {
    return '';
  }

  if (!drillKey) {
    return uriEncodeEventQuery(queryAttrs);
  } else {
    const drillQueryAttrs = getProperties(
      queryAttrs,
      'serviceId',
      'startTime',
      'endTime',
      'metaFilter'
    );
    const drillConditions = [].concat(
      get(queryAttrs, 'metaFilter.conditions') || []
    );
    drillConditions.pushObject({
      queryString: `${drillKey} ${drillOperator} ${drillValue}`,
      meta: drillKey,
      operator: drillOperator,
      value: drillValue
    });
    set(drillQueryAttrs, 'metaFilter', { conditions: drillConditions });

    return uriEncodeEventQuery(drillQueryAttrs);
  }
}

export default Helper.helper(eventQueryUri);
