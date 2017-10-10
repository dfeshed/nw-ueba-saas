import Component from 'ember-component';
import { connect } from 'ember-redux';
import { and } from 'ember-computed-decorators';
import {
  setQueryTimeRange,
  setServiceId
} from 'investigate-events/actions/interaction-creators';

const stateToComputed = ({ investigate }) => ({
  endTime: investigate.queryNode.endTime,
  queryString: investigate.queryNode.queryString,
  serviceId: investigate.queryNode.serviceId,
  services: investigate.services.data,
  startTime: investigate.queryNode.startTime
});

const dispatchToActions = { setQueryTimeRange, setServiceId };

const {
  warn
} = console;

const TIME_RANGES = [
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
];

const QueryBarComponent = Component.extend({
  classNames: 'rsa-investigate-query-bar',

  /**
   * Configurable callback to be invoked when user submits the query.
   * @type {function}
   * @public
   */
  onSubmit: undefined,

  /**
   * Array of available time ranges for user to pick from.
   * @type {object[]}
   * @private
   */
  timeRanges: TIME_RANGES,

  /**
   * The selected service object from the `services` list.
   * @type {object[]}
   * @private
   */
  selectedService: undefined,

  /**
   * The selected time range object from the `timeRanges` list.
   * @type {object[]}
   * @private
   */
  selectedTimeRange: undefined,

  @and('serviceId', 'selectedTimeRange')
  hasRequiredValuesToQuery: false,

  actions: {
    submit() {
      if (this.get('hasRequiredValuesToQuery')) {
        const fn = this.get('onSubmit');
        if (fn instanceof Function) {
          const serviceId = this.get('serviceId');
          const startTime = this.get('startTime');
          const endTime = this.get('endTime');
          const queryString = this.get('queryString');
          fn(serviceId, startTime, endTime, queryString);
        } else {
          warn('Invalid onSubmit action defined for query-bar.');
        }
      }
    },

    updateService(selectedService) {
      this.set('selectedService', selectedService);
      this.send('setServiceId', selectedService.id);
    },

    updateRange(selectedTimeRange) {
      this.set('selectedTimeRange', selectedTimeRange);
      this.send('setQueryTimeRange', selectedTimeRange);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryBarComponent);
