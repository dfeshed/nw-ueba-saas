import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed, { and } from 'ember-computed-decorators';
import service from 'ember-service/inject';
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
  isSummaryRetrieveError: state.investigate.services.isSummaryRetrieveError,
  summaryErrorMessage: state.investigate.services.summaryErrorMessage,
  services: state.investigate.services.serviceData
});

const dispatchToActions = { setQueryTimeRange, setService };

const QueryBarComponent = Component.extend({
  classNames: 'rsa-investigate-query-bar',
  i18n: service(),

  /**
   * Array of available time ranges for user to pick from.
   * @type {object[]}
   * @private
   */
  timeRanges: TIME_RANGES,

  /**
   * @public
   * Returns a string that is used to wire the triggerClass property on the powerSelect.
   * Setting class `is-error` shows the service in red color.
   */
  @computed('selectedService.id', 'hasSummaryData', 'isSummaryRetrieveError')
  powerSelectClass(id, hasSummaryData, isSummaryRetrieveError) {
    return (id && !hasSummaryData && isSummaryRetrieveError) ? 'is-error' : 'null';
  },

  /**
   * @public
   * Tooltip (browser title attribute) for the services icon.
   * Using regex to trim any content before the first colon from the error message.
   */
  @computed('hasSummaryData', 'summaryErrorMessage', 'i18n')
  summaryErrorTooltip: (hasSummaryData, errMsg, i18n) => {
    // Check if error message is set and return that after trimming.
    // Before regex - rsa.com.nextgen.classException: Failed to connect to broker:50003
    // After regex - Failed to connect to broker:50003
    if (!hasSummaryData && errMsg) {
      return errMsg.match(/:(.*)/g).pop().replace(':', '');
    }
    return i18n.t('investigate.services.noData');
  },

  @and('selectedService.id', 'hasSummaryData', 'selectedTimeRange')
  hasRequiredValuesToQuery: false
});

export default connect(stateToComputed, dispatchToActions)(QueryBarComponent);
