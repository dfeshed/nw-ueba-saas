import Component from 'ember-component';
import { connect } from 'ember-redux';
import { and } from 'ember-computed-decorators';
import { hasSummaryData } from 'investigate-events/reducers/investigate/services/selectors';
import {
  setQueryTimeRange,
  setServiceId
} from 'investigate-events/actions/interaction-creators';

const stateToComputed = (state) => ({
  hasSummaryData: hasSummaryData(state),
  queryString: state.investigate.queryNode.queryString,
  serviceId: state.investigate.queryNode.serviceId,
  services: state.investigate.services.data
});

const dispatchToActions = { setQueryTimeRange, setServiceId };

const TIME_RANGES = [
  { id: 'LAST_5_MINUTES', name: 'Last 5 Minutes', value: 5, unit: 'minutes' },
  { id: 'LAST_10_MINUTES', name: 'Last 10 Minutes', value: 10, unit: 'minutes' },
  { id: 'LAST_15_MINUTES', name: 'Last 15 Minutes', value: 15, unit: 'minutes' },
  { id: 'LAST_30_MINUTES', name: 'Last 30 Minutes', value: 30, unit: 'minutes' },
  { id: 'LAST_HOUR', name: 'Last 1 Hour', value: 1, unit: 'hours' },
  { id: 'LAST_3_HOURS', name: 'Last 3 Hours', value: 3, unit: 'hours' },
  { id: 'LAST_6_HOURS', name: 'Last 6 Hours', value: 6, unit: 'hours' },
  { id: 'LAST_12_HOURS', name: 'Last 12 Hours', value: 12, unit: 'hours' },
  { id: 'LAST_24_HOURS', name: 'Last 24 Hours', value: 1, unit: 'days' },
  { id: 'LAST_2_DAYS', name: 'Last 2 Days', value: 2, unit: 'days' },
  { id: 'LAST_5_DAYS', name: 'Last 5 Days', value: 5, unit: 'days' },
  { id: 'LAST_7_DAYS', name: 'Last 7 Days', value: 7, unit: 'days' },
  { id: 'LAST_14_DAYS', name: 'Last 14 Days', value: 14, unit: 'days' },
  { id: 'LAST_30_DAYS', name: 'Last 30 Days', value: 1, unit: 'months' },
  { id: 'ALL_DATA', name: 'All Data', value: 0, unit: 'all' }
];

const QueryBarComponent = Component.extend({
  classNames: 'rsa-investigate-query-bar',

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
