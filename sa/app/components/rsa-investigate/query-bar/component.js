/**
 * @file Investigate Query Bar
 * A UI for the user to query the events of a NetWitness Core service.
 * @public
 */
import Ember from 'ember';

const {
  computed,
  get,
  isEmpty,
  Component,
  Logger,
  $
} = Ember;

const TYPE = {
  BROKER: 'BROKER',
  CONCENTRATOR: 'CONCENTRATOR'
};

export default Component.extend({
  classNames: 'rsa-investigate-query-bar',

  /**
   * Array of available Core services (brokers, concentrators, etc).
   * @type {object[]}
   * @public
   */
  services: undefined,

  /**
   * Optional status of the promise to fetch `services`. If this is set to 'wait', the UI will show a wait
   * message in the place where the list of services would otherwise be shown.
   * @type {string}
   * @public
   *
   */
  servicesStatus: undefined,

  /**
   * Array of available time ranges for user to pick from.
   * @type {object[]}
   * @public
   */
  timeRanges: computed(function() {
    return [
      { id: 'LAST_24_HOURS', name: 'Last 24 Hours', seconds: 24 * 60 * 60 },
      { id: 'LAST_12_HOURS', name: 'Last 12 Hours', seconds: 12 * 60 * 60 },
      { id: 'LAST_6_HOURS', name: 'Last 6 Hours', seconds: 6 * 60 * 60 },
      { id: 'LAST_3_HOURS', name: 'Last 3 Hours', seconds: 3 * 60 * 60 },
      { id: 'LAST_HOUR', name: 'Last Hour', seconds: 60 * 60 },
      { id: 'LAST_5_MINUTES', name: 'Last 5 Minutes', seconds: 5 * 60 },
      { id: 'LAST_10_MINUTES', name: 'Last 10 Minutes', seconds: 10 * 60 },
      { id: 'LAST_15_MINUTES', name: 'Last 15 Minutes', seconds: 15 * 60 },
      { id: 'LAST_30_MINUTES', name: 'Last 30 Minutes', seconds: 30 * 60 },
      { id: 'LAST_2_DAYS', name: 'Last 2 Days', seconds: 2 * 24 * 60 * 60 },
      { id: 'LAST_5_DAYS', name: 'Last 5 Days', seconds: 5 * 24 * 60 * 60 },
      { id: 'ALL_DATA', name: 'All Data', seconds: 0 }
    ];
  }),

  /**
   * Configurable callback to be invoked when user submits the query.
   * This function will be invoked with the following arguments:
   * @param {string} serviceId the ID of the core-service to which the query is directed
   * @param {number} startTime start of the time range for the query filter (in UTC seconds)
   * @param {number} endTime end of the time range for the query filter (in UTC seconds)
   * @type {function}
   * @public
   */
  onSubmit: undefined,

  /**
   * Array of IDs of the services selected from the `services` list. Has at most one array item, possibly empty.
   * By default, we auto-select the first broker we find in the list; if none, then the first service, if any.
   * @type {object[]}
   * @public
   */
  _selectedServiceIds: undefined,
  selectedServiceIds: computed('services', {
    get() {
      if (this._selectedServiceIds) {
        return this._selectedServiceIds;
      } else {
        let services = this.get('services');
        let service = !services ? null : services.findBy('type', TYPE.BROKER) ||
          services.findBy('type', TYPE.CONCENTRATOR) ||
          services[0];
        return service ? [ get(service, 'id') ] : [];
      }
    },
    set(key, value) {
      this._selectedServiceIds = value;
      return value;
    }
  }),

  /**
   * Array of IDs of the time ranges selected from the `timeRanges` list. Has at most one array item, possibly empty.
   * By default, we auto-select the first range we find in the list, if any.
   * @type {object[]}
   * @public
   */
  _selectedTimeRangeIds: undefined,
  selectedTimeRangeIds: computed('timeRanges', {
    get() {
      if (this._selectedTimeRangeIds) {
        return this._selectedTimeRangeIds;
      } else {
        let first = this.get('timeRanges.firstObject.id');
        return first ? [ first ] : [];
      }
    },
    set(key, value) {
      this._selectedTimeRangeIds = value;
      return value;
    }
  }),

  // Resolves to `true` only if all the required user inputs/selections have been made.
  _submitIsEnabled: computed('selectedServiceIds', function() {
    return !isEmpty(this.get('selectedServiceIds.firstObject')) &&
      !isEmpty(this.get('selectedTimeRangeIds.firstObject'));
  }),

  actions: {
    // Kicks off a query by invoking the configurable `onSubmit` callback.
    // For now, submits hard-coded values to illustrate the workflow.
    // In an upcoming PR we will fill out this component with dynamic content and submit dynamic values.
    submit() {
      let fn = this.get('onSubmit');
      Logger.assert(
        $.isFunction(fn),
        'Invalid onSubmit action defined for rsa-query-bar. Action aborted.'
      );
      let serviceId = this.get('selectedServiceIds.firstObject');
      let timeRangeId = this.get('selectedTimeRangeIds.firstObject');
      let timeRange = (this.get('timeRanges') || []).findBy('id', timeRangeId) || {};
      let seconds = get(timeRange, 'seconds');
      let nowInSeconds = parseInt(+(new Date()) / 1000, 10);
      fn(
        serviceId,
        nowInSeconds - seconds,
        nowInSeconds
      );
    }
  }
});
