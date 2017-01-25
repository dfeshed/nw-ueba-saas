import Ember from 'ember';

const { Object: EmberObject } = Ember;

export default EmberObject.extend({
  /**
   * The max number of meta values to be returned from the server request.
   * @type {number}
   * @public
   */
  size: 20,

  /**
   * Optional query optimization to stop processing large event counts.
   * When an event count exceeds `threshold` for a certain value, that value will cease being counted.
   * @type {number}
   * @public
   */
  threshold: 50000,

  /**
   * Specifies the metric by which the meta values will be aggregated; either 'sessions' (count of events), 'size'
   * (KB size of events) or 'packets' (count of packets within the events).
   * @type {string}
   * @public
   */
  metric: 'sessions',

  /**
   * Specifies the field by which the meta values will be sorted; either 'value' (the actual meta value, such
   * as an IP address, hostname, etc) or 'total' (the computed metric for that meta value).
   * @type {string}
   * @public
   */
  sortField: 'total',

  /**
   * Specifies the sort order of the meta values; either 'descending' or 'ascending'.
   * @type {string}
   * @public
   */
  sortOrder: 'descending'
});
