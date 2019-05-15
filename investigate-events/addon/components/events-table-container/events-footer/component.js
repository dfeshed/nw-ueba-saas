import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import { thousandFormat } from 'component-lib/utils/numberFormats';
import {
  actualEventCount,
  allExpectedDataLoaded,
  areEventsStreaming,
  isCanceled,
  isEventResultsError,
  noEvents
} from 'investigate-events/reducers/investigate/event-results/selectors';

const stateToComputed = (state) => ({
  actualEventCount: thousandFormat(actualEventCount(state)),
  allExpectedDataLoaded: allExpectedDataLoaded(state),
  areEventsStreaming: areEventsStreaming(state),
  items: state.investigate.eventResults.data,
  isCanceled: isCanceled(state),
  isEventResultsError: isEventResultsError(state),
  isQueryExecutedByColumnGroup: state.investigate.data.isQueryExecutedByColumnGroup,
  isQueryExecutedBySort: state.investigate.data.isQueryExecutedBySort,
  maxEvents: thousandFormat(state.investigate.eventResults.streamLimit),
  noEvents: noEvents(state),
  totalCount: thousandFormat(state.investigate.eventCount.data),
  status: state.investigate.eventResults.status
});

const EventsFooter = Component.extend({

  @computed('items')
  hasResults(results) {
    return !!results && results.length > 0;
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