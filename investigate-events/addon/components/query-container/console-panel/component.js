import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

import {
  hasError,
  hasWarning,
  isQueryComplete,
  decoratedDevices,
  warningsWithServiceName,
  errorsWithServiceName
} from 'investigate-events/reducers/investigate/query-stats/selectors';
import { isCanceled, percentageOfEventsDataReturned } from 'investigate-events/reducers/investigate/event-results/selectors';
import { queriedService } from 'investigate-events/reducers/investigate/services/selectors';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';

const stateToComputed = (state) => ({
  startTime: state.investigate.queryNode.previousQueryParams.startTime,
  endTime: state.investigate.queryNode.previousQueryParams.endTime,
  description: state.investigate.queryStats.description,
  warnings: warningsWithServiceName(state),
  errors: errorsWithServiceName(state),
  isCanceled: isCanceled(state),
  isQueryComplete: isQueryComplete(state),
  hasError: hasError(state),
  hasWarning: hasWarning(state),
  devices: decoratedDevices(state),
  filters: encodeMetaFilterConditions(state.investigate.queryNode.previousQueryParams.metaFilter),
  queriedService: queriedService(state),
  percentageOfEventsDataReturned: percentageOfEventsDataReturned(state)
});

const ConsolePanel = Component.extend({
  timezone: service(),

  classNames: ['console-panel'],
  classNameBindings: [
    'hasError',
    'hasWarning'
  ],

  format: '"YYYY-MM-DD HH:mm:ss"',

  @computed('endTime') formattedEndDate: (endTime) => endTime * 1000,
  @computed('startTime') formattedStartDate: (startTime) => startTime * 1000,

  @computed('isQueryComplete', 'retrievalComplete')
  isRetrieving: (isQueryComplete, retrievalComplete) => {
    return isQueryComplete && !retrievalComplete;
  },

  @computed('percentageOfEventsDataReturned')
  retrievalComplete: (percentageOfEventsDataReturned) => {
    return percentageOfEventsDataReturned === 100;
  },

  @computed('description', 'isQueryComplete', 'retrievalComplete', 'i18n', 'isCanceled')
  progressLabel: (description, isQueryComplete, retrievalComplete, i18n, isCanceled) => {
    if (isCanceled) {
      return i18n.t('investigate.queryStats.canceled');
    } else if (!isQueryComplete) {
      if (description) {
        if (description.toLowerCase() === 'queued') {
          return description;
        } else if (description.toLowerCase() === 'executing') {
          return description;
        } else if (description) {
          return `${i18n.t('investigate.queryStats.executing')} - ${description}`;
        }
      } else {
        return i18n.t('investigate.queryStats.complete');
      }
    } else {
      if (retrievalComplete) {
        return i18n.t('investigate.queryStats.complete');
      } else if (isQueryComplete) {
        return i18n.t('investigate.queryStats.retrieving');
      }
    }
  }

});

export default connect(stateToComputed)(ConsolePanel);
