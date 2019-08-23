import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import { TEXT_FILTER } from 'investigate-events/constants/pill';
import {
  hasError,
  hasWarning,
  isQueryComplete,
  decoratedDevices,
  warningsWithServiceName,
  errorsWithServiceName,
  isMixedMode,
  hasOfflineServices
} from 'investigate-events/reducers/investigate/query-stats/selectors';
import { isCanceled, percentageOfEventsDataReturned } from 'investigate-events/reducers/investigate/event-results/selectors';
import { queriedService } from 'investigate-events/reducers/investigate/services/selectors';
import { metaFiltersAsString } from 'investigate-events/util/query-parsing';

const stateToComputed = (state) => ({
  startTime: state.investigate.queryNode.previousQueryParams.startTime,
  endTime: state.investigate.queryNode.previousQueryParams.endTime,
  description: state.investigate.queryStats.description,
  hasOfflineServices: hasOfflineServices(state),
  warnings: warningsWithServiceName(state),
  errors: errorsWithServiceName(state),
  isCanceled: isCanceled(state),
  isQueryComplete: isQueryComplete(state),
  hasError: hasError(state),
  hasWarning: hasWarning(state),
  devices: decoratedDevices(state),
  queryFilters: state.investigate.queryNode.previousQueryParams.metaFilter,
  queriedService: queriedService(state),
  percentageOfEventsDataReturned: percentageOfEventsDataReturned(state),
  isMixedMode: isMixedMode(state)
});

const ConsolePanel = Component.extend({
  timezone: service(),

  classNames: ['console-panel'],
  classNameBindings: [
    'isMixedMode',
    'hasError',
    'hasWarning',
    'hasOfflineServices'
  ],

  format: '"YYYY-MM-DD HH:mm:ss"',

  @computed('queryFilters')
  filters: (queryFilters) => {
    const sansTextFilter = queryFilters.filter((f) => f.type !== TEXT_FILTER);
    const textFilter = queryFilters.find((f) => f.type === TEXT_FILTER);
    return {
      metaFilters: metaFiltersAsString(sansTextFilter, false),
      textFilter: textFilter ? textFilter.searchTerm : undefined
    };
  },

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

  @computed('description', 'isQueryComplete', 'retrievalComplete', 'i18n', 'isCanceled', 'hasError')
  progressLabel: (description, isQueryComplete, retrievalComplete, i18n, isCanceled, hasError) => {
    if (hasError) {
      return i18n.t('investigate.queryStats.error');
    } else if (isCanceled) {
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
