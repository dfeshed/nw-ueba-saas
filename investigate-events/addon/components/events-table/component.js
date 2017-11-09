import Component from 'ember-component';
import computed from 'ember-computed-decorators';

import { connect } from 'ember-redux';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';

const stateToComputed = (state) => ({
  aliases: state.investigate.dictionaries.aliases,
  language: state.investigate.dictionaries.language,
  reconSize: state.investigate.data.reconSize
});

const EventsTable = Component.extend({
  classNames: 'rsa-investigate-events-table',

  // Passed along to progress bar.
  status: undefined,
  percent: undefined,

  // Passed along to data table.
  items: undefined,
  columnsConfig: undefined,
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
  toggleEventsClass: (size) => (size !== RECON_PANEL_SIZES.MAX) ?
    'shrink-diagonal-2' : 'expand-diagonal-4',

  @computed('reconSize')
  toggleEventsTitle: (size) => (size !== RECON_PANEL_SIZES.MAX) ?
    'investigate.events.shrink' : 'investigate.events.expand'
});

export default connect(stateToComputed)(EventsTable);
