import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';

import { thousandFormat } from 'component-lib/utils/numberFormats';
import { classicEventsURL } from 'component-lib/utils/build-url';
import {
  actualEventCount,
  allExpectedDataLoaded,
  areEventsStreaming,
  isCanceled,
  isEventResultsError,
  noEvents,
  hideEventsForReQuery
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { hadTextPill, queryNodeValuesForClassicUrl } from 'investigate-events/reducers/investigate/query-node/selectors';
import { summaryValuesForClassicUrl } from 'investigate-events/reducers/investigate/services/selectors';

const stateToComputed = (state) => ({
  actualEventCount: thousandFormat(actualEventCount(state)),
  allExpectedDataLoaded: allExpectedDataLoaded(state),
  areEventsStreaming: areEventsStreaming(state),
  hadTextPill: hadTextPill(state),
  hideEventsForReQuery: hideEventsForReQuery(state),
  items: state.investigate.eventResults.data,
  isCanceled: isCanceled(state),
  isEventResultsError: isEventResultsError(state),
  isQueryExecutedByColumnGroup: state.investigate.data.isQueryExecutedByColumnGroup,
  isQueryExecutedBySort: state.investigate.data.isQueryExecutedBySort,
  maxEvents: thousandFormat(state.investigate.eventResults.streamLimit),
  noEvents: noEvents(state),
  queryNodeValuesForClassicUrl: queryNodeValuesForClassicUrl(state),
  totalCount: thousandFormat(state.investigate.eventCount.data),
  summaryValuesForClassicUrl: summaryValuesForClassicUrl(state),
  status: state.investigate.eventResults.status,
  threshold: thousandFormat(state.investigate.eventCount.threshold),
  isAtThreshold: resultCountAtThreshold(state)

});

const EventsFooter = Component.extend({

  @computed('items')
  hasResults(results) {
    return !!results && results.length > 0;
  },

  @computed('hadTextPill', 'queryNodeValuesForClassicUrl', 'summaryValuesForClassicUrl')
  footerMessage(hadTextPill, queryNodeValuesForClassicUrl, summaryValuesForClassicUrl) {
    const i18n = this.get('i18n');
    let message = i18n.t('investigate.allResultsLoaded');
    if (hadTextPill) {
      const url = `${window.location.origin}/${classicEventsURL({ ...queryNodeValuesForClassicUrl, ...summaryValuesForClassicUrl })}`;
      message = i18n.t('investigate.textSearchLimitedResults', { url, message, htmlSafe: true }).string;
    }
    return message;
  },

  @computed('isCanceled', 'isEventResultsError', 'actualEventCount')
  hasPartialResults(isCanceled, isEventResultsError, actualEventCount) {
    const i18n = this.get('i18n');
    if (isEventResultsError && actualEventCount) {
      return i18n.t('investigate.empty.errorWithPartial');
    } else if (isCanceled && actualEventCount) {
      return i18n.t('investigate.empty.canceledWithPartial');
    }
  }

});

export default connect(stateToComputed)(EventsFooter);
