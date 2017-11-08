import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed, { and } from 'ember-computed-decorators';
import {
  hasSummaryData,
  selectedService
} from 'investigate-events/reducers/investigate/services/selectors';
import { selectedTimeRangeId } from 'investigate-events/reducers/investigate/query-node/selectors';
import {
  setQueryTimeRange,
  setService
} from 'investigate-events/actions/interaction-creators';
import TIME_RANGES from 'investigate-events/constants/time-ranges';

const stateToComputed = (state) => ({
  hasSummaryData: hasSummaryData(state),
  selectedService: selectedService(state),
  selectedTimeRangeId: selectedTimeRangeId(state),
  services: state.investigate.services.data
});

const dispatchToActions = { setQueryTimeRange, setService };

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
