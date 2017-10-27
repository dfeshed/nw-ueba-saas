import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed, { and } from 'ember-computed-decorators';
import { hasSummaryData, selectedService } from 'investigate-events/reducers/investigate/services/selectors';
import {
  setQueryTimeRange,
  setService
} from 'investigate-events/actions/interaction-creators';

const stateToComputed = (state) => ({
  hasSummaryData: hasSummaryData(state),
  selectedService: selectedService(state),
  selectedTimeRangeId: state.investigate.queryNode.selectedTimeRangeId,
  services: state.investigate.services.data
});

const dispatchToActions = { setQueryTimeRange, setService };

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
   * @public
   * Loop over the timeRanges array and return the timeRange object that is selected.
   * This object is wired into the power-select in the template.
   * {id: "LAST_30_DAYS", value: 1, unit: "months"}
   */
  @computed('timeRanges', 'selectedTimeRangeId')
  selectedTimeRange: (timeRanges, selectedTimeRangeId) => {
    return timeRanges.find((e) => e.id === selectedTimeRangeId);
  },

  @and('selectedService.id', 'hasSummaryData', 'selectedTimeRangeId')
  hasRequiredValuesToQuery: false
});

export default connect(stateToComputed, dispatchToActions)(QueryBarComponent);
