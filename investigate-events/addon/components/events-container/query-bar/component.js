import Component from 'ember-component';
import { connect } from 'ember-redux';
import { and } from 'ember-computed-decorators';
import {
  hasSummaryData,
  selectedService
} from 'investigate-events/reducers/investigate/services/selectors';
import { selectedTimeRange } from 'investigate-events/reducers/investigate/query-node/selectors';
import {
  setQueryTimeRange,
  setService
} from 'investigate-events/actions/interaction-creators';
import TIME_RANGES from 'investigate-events/constants/time-ranges';

const stateToComputed = (state) => ({
  hasSummaryData: hasSummaryData(state),
  selectedService: selectedService(state),
  selectedTimeRange: selectedTimeRange(state),
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

  @and('selectedService.id', 'hasSummaryData', 'selectedTimeRange')
  hasRequiredValuesToQuery: false
});

export default connect(stateToComputed, dispatchToActions)(QueryBarComponent);
