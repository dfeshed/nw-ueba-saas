/**
 * @file Query class
 * Represents an query for events from a Netwitness Core Service.
 * @public
 */
import Ember from 'ember';

const { Object: EmberObject } = Ember;

export default EmberObject.extend({
  /**
   * ID of the Core service targeted by this query.
   * @type {string}
   * @public
   */
  serviceId: '',

  /**
   * Lower bound of the event times to be included in this query, in seconds since 1970.
   * @type {number}
   * @public
   */
  startTime: 0,

  /**
   * Upper bound of the event times to be included in this query, in seconds since 1970.
   * @type {number}
   * @public
   */
  endTime: 0,

  /**
   * Where clause for filtering Core results (e.g., ip.src=X).
   * @type {string}
   * @public
   */
  metaFilter: ''
});
