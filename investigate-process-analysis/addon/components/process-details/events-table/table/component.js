import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  eventsData,
  eventsCount,
  eventsTableConfig,
  eventsSortField
 } from 'investigate-process-analysis/reducers/process-tree/selectors';
import { setSortField } from 'investigate-process-analysis/actions/creators/events-creators';

const stateToComputed = (state) => ({
  eventsData: eventsData(state),
  config: eventsTableConfig(),
  eventsCount: eventsCount(state),
  selectedSortField: eventsSortField(state)
});

const dispatchToActions = {
  setSortField
};
const processEventsTable = Component.extend({
  classNames: ['process-events-table'],
  options: [
    { label: 'Event Time', field: 'evemt.time', type: 'ASC' },
    { label: 'Event Time', field: 'event.time', type: 'DESC' }
  ]
});
export default connect(stateToComputed, dispatchToActions)(processEventsTable);