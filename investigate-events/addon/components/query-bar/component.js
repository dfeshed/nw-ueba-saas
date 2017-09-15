import Component from 'ember-component';
import get from 'ember-metal/get';
import { assert } from 'ember-metal/utils';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import $ from 'jquery';
import { serviceSelected } from 'investigate-events/actions/interaction-creators';

const stateToComputed = ({ data, services }) => ({
  endpointId: data.endpointId,
  services: services.data
});

const dispatchToActions = { serviceSelected };

const QueryBarComponent = Component.extend({
  classNames: 'rsa-investigate-query-bar',
  queryString: '',

  /**
   * Array of available time ranges for user to pick from.
   * @type {object[]}
   * @public
   */
  timeRanges: [
    { id: 'LAST_5_MINUTES', name: 'Last 5 Minutes', seconds: 5 * 60 },
    { id: 'LAST_10_MINUTES', name: 'Last 10 Minutes', seconds: 10 * 60 },
    { id: 'LAST_15_MINUTES', name: 'Last 15 Minutes', seconds: 15 * 60 },
    { id: 'LAST_30_MINUTES', name: 'Last 30 Minutes', seconds: 30 * 60 },
    { id: 'LAST_HOUR', name: 'Last 1 Hour', seconds: 60 * 60 },
    { id: 'LAST_3_HOURS', name: 'Last 3 Hours', seconds: 3 * 60 * 60 },
    { id: 'LAST_6_HOURS', name: 'Last 6 Hours', seconds: 6 * 60 * 60 },
    { id: 'LAST_12_HOURS', name: 'Last 12 Hours', seconds: 12 * 60 * 60 },
    { id: 'LAST_24_HOURS', name: 'Last 24 Hours', seconds: 24 * 60 * 60 },
    { id: 'LAST_2_DAYS', name: 'Last 2 Days', seconds: 2 * 24 * 60 * 60 },
    { id: 'LAST_5_DAYS', name: 'Last 5 Days', seconds: 5 * 24 * 60 * 60 },
    { id: 'ALL_DATA', name: 'All Data', seconds: 0 }
  ],

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
  selectedService: undefined,

  /**
   * Array of IDs of the time ranges selected from the `timeRanges` list. Has at most one array item, possibly empty.
   * By default, we auto-select the first range we find in the list, if any.
   * @type {object[]}
   * @public
   */
  selectedTimeRange: undefined,

  // Resolves to `true` only if all the required user inputs/selections have been made.
  @computed('endpointId', 'selectedTimeRange')
  submitDisabled: (endpointId, timeRange) => !(endpointId && timeRange),

  actions: {
    // Kicks off a query by invoking the configurable `onSubmit` callback.
    // For now, submits hard-coded values to illustrate the workflow.
    // In an upcoming PR we will fill out this component with dynamic content and submit dynamic values.
    submit() {
      const submitEnabled = !this.get('submitDisabled');
      if (submitEnabled) {
        const fn = this.get('onSubmit');
        assert(
          $.isFunction(fn),
          'Invalid onSubmit action defined for rsa-query-bar. Action aborted.'
        );
        const serviceId = this.get('endpointId');
        const timeRangeId = this.get('selectedTimeRange.id');
        const timeRange = (this.get('timeRanges') || []).findBy('id', timeRangeId) || {};
        const seconds = get(timeRange, 'seconds');
        const toDate = +new Date() / 1000 | 0; // "/ 1000 | 0" removes milliseconds
        // If user selects "All Data", seconds is zero; submit a start time of zero.
        const fromDate = seconds ? toDate - seconds : 0;
        const queryString = this.get('queryString');
        fn(
          serviceId,
          fromDate,
          toDate,
          queryString
        );
      }
    },

    /**
     * Updates the selected service, based on the one you click
     * @param selectedService The service clicked in the power select
     * @private
     */
    updateService(selectedService) {
      // Store off the service object for use internally to this component
      this.set('selectedService', selectedService);
      // Save the id of the endpoint to state
      this.send('serviceSelected', selectedService.id);
    },

    /**
     * Updates the selected time range, based on the one you click
     * @param selectedTimeRange The time range clicked in the power select
     * @private
     */
    updateRange(selectedTimeRange) {
      this.set('selectedTimeRange', selectedTimeRange);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryBarComponent);
