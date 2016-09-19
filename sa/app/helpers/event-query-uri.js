import Ember from 'ember';
import { uriEncodeEventQuery } from '../protected/investigate/actions/helpers/query-utils';

const {
  get,
  getProperties,
  set,
  Helper
} = Ember;

/**
 * Composes a URI component string for a given query definition, plus (optional) drill path.
 * Useful for creating hyperlinks for drilling to the results of a Netwitness Core query for Events.
 * @param {object} queryAttrs @see protected/investigate/state/query-definition
 * @param {string} [drillKey] Optional meta key to be appended to the given query.
 * @param {*} [drillValue] Optional meta key value to be appended to the given query.
 * @returns {*}
 * @public
 */
export function eventQueryUri([ queryAttrs, drillKey, drillValue ] /* , hash */) {
  if (!queryAttrs) {
    return '';
  }

  if (!drillKey) {
    return uriEncodeEventQuery(queryAttrs);
  } else {
    let drillQueryAttrs = getProperties(
      queryAttrs,
      'serviceId',
      'startTime',
      'endTime',
      'metaFilter'
    );
    let drillConditions = [].concat(
      get(queryAttrs, 'metaFilter.conditions') || []
    );
    drillConditions.pushObject({
      key: drillKey,
      value: drillValue
    });
    set(drillQueryAttrs, 'metaFilter', { conditions: drillConditions });

    return uriEncodeEventQuery(drillQueryAttrs);
  }
}

export default Helper.helper(eventQueryUri);
