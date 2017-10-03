import Component from 'ember-component';
import computed from 'ember-computed-decorators';

import { connect } from 'ember-redux';

const stateToComputed = ({ data: { reconSize } }) => ({
  reconSize
});

const EventsTable = Component.extend({
  classNames: 'rsa-investigate-events-table',

  // Passed along to progress bar.
  status: undefined,
  percent: undefined,

  // Passed along to data table.
  items: undefined,
  columnsConfig: undefined,
  language: undefined,
  aliases: undefined,
  rowClickAction: undefined,
  loadLogsAction: undefined,

  // Passed along to counter.
  loadMoreAction: undefined,
  stopAction: undefined,
  retryAction: undefined,
  totalCount: undefined,
  totalStatus: undefined,
  totalThreshold: undefined,
  totalRetryAction: undefined,

  @computed('reconSize')
  toggleEventsClass: (size) => (size !== 'max') ? 'shrink-diagonal-2' : 'expand-diagonal-4',

  @computed('reconSize')
  toggleEventsTitle: (size) => (size !== 'max') ? 'investigate.events.shrink' : 'investigate.events.expand'
});

export default connect(stateToComputed)(EventsTable);