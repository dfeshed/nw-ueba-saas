import Ember from 'ember';

const {
  Component
} = Ember;

export default Component.extend({
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
  totalRetryAction: undefined
});
