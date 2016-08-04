import Ember from 'ember';

const {
  assert,
  getProperties
} = Ember;

/**
 * Given an object representing a query, computes the input parameters required to submit that
 * query to the server.
 * @param {object} query The query object. @see investigate/state/query
 * @public
 */
export default function(query) {
  let {
      serviceId, startTime, endTime, metaFilter
    } = getProperties(
      query || {}, 'serviceId', 'startTime', 'endTime', 'metaFilter'
    );

  assert(
    serviceId && startTime && endTime,
    'Cannot make a core query without a service id, start time & end time.'
  );

  return {
    filter: [
      { field: 'endpointId', value: serviceId },
      { field: 'timeRange', range: { from: startTime, to: endTime } },
      { field: 'query', value: metaFilter || '' }
    ]
  };
}
