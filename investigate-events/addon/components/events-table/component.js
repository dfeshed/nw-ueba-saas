import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';
import { getSelectedColumnGroup, getColumnGroups, getColumns } from 'investigate-events/reducers/investigate/data-selectors';
import {
  percentageOfEventsDataReturned,
  selectedIndex
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { eventsGetMore, eventsLogsGet } from 'investigate-events/actions/events-creators';

const stateToComputed = (state) => ({
  status: state.investigate.eventResults.status,
  percent: percentageOfEventsDataReturned(state),
  selectedIndex: selectedIndex(state),
  items: state.investigate.eventResults.data,
  aliases: state.investigate.dictionaries.aliases,
  language: state.investigate.dictionaries.language,
  reconSize: state.investigate.data.reconSize,
  totalCount: state.investigate.eventCount.data,
  isOpen: state.investigate.data.isReconOpen,
  columnGroups: getColumnGroups(state),
  selectedColumnGroup: getSelectedColumnGroup(state),
  columns: getColumns(state)
});

const dispatchToActions = {
  setColumnGroup,
  eventsGetMore,
  eventsLogsGet
};

const EventsTable = Component.extend({
  classNames: 'rsa-investigate-events-table',

  @computed('reconSize')
  toggleEvents(size) {
    const isSizeNotMax = size !== RECON_PANEL_SIZES.MAX;
    return {
      class: isSizeNotMax ? 'shrink-diagonal-2' : 'expand-diagonal-4',
      title: isSizeNotMax ? 'investigate.events.shrink' : 'investigate.events.expand'
    };
  }
});

export default connect(stateToComputed, dispatchToActions)(EventsTable);
