/**
 * @file Query class
 * Represents an query for events from a Netwitness Core Service.
 * @public
 */
import Ember from 'ember';

const { get, computed, Object: EmberObject } = Ember;

const Query = EmberObject.extend({
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
   * An object representing a where clause for filtering Core results by meta values (e.g., ip.src=X).
   * @type {object}
   * @public
   */
  metaFilter: computed(() => {
    return {
      uri: '',
      conditions: []
    };
  }),

  /**
   * Creates a clone of this instance. The clone's property values will be copied from this instance's property values,
   * but they will be de-coupled (i.e., changing one instance's properties will not affect the other).
   * @returns {object} Newly cloned instance of this class.
   * @public
   */
  clone() {
    let attrs = this.getProperties('serviceId', 'startTime', 'endTime');
    attrs.metaFilter = {
      uri: this.get('metaFilter.uri'),
      conditions: [].concat(this.get('metaFilter.conditions'))
    };
    return Query.create(attrs);
  },

  isEqual(params) {
    if (params) {
      return (get(params, 'serviceId') === this.get('serviceId')) &&
        (get(params, 'startTime') === this.get('startTime')) &&
        (get(params, 'endTime') === this.get('endTime')) &&
        (get(params, 'metaFilter.uri') === this.get('metaFilter.uri'));
    }
    return false;
  }
});

export default Query;
