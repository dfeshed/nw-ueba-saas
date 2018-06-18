import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  eventsData,
  eventsFilteredCount,
  eventsTableConfig,
  eventsSortField
 } from 'investigate-process-analysis/reducers/process-tree/selectors';
import { setSortField } from 'investigate-process-analysis/actions/creators/events-creators';

const stateToComputed = (state) => ({
  eventsData: eventsData(state),
  config: eventsTableConfig(),
  eventsFilteredCount: eventsFilteredCount(state),
  selectedSortType: eventsSortField(state)
});

const dispatchToActions = {
  setSortField
};
const processEventsTable = Component.extend({
  classNames: ['process-events-table'],
  actions: {
    sort(column) {
      column.set('isDescending', !column.isDescending);
      this.send('setSortField', { field: column.field, isDescending: column.isDescending });
    }
  }

});
export default connect(stateToComputed, dispatchToActions)(processEventsTable);