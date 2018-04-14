import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getColumnGroups } from 'investigate-events/reducers/investigate/data-selectors';
import { percentageOfEventsDataReturned } from 'investigate-events/reducers/investigate/event-results/selectors';

const stateToComputed = (state) => ({
  status: state.investigate.eventResults.status,
  columnGroups: getColumnGroups(state),
  percent: percentageOfEventsDataReturned(state)
});

const EventsTableContainer = Component.extend({
  classNames: ['rsa-investigate-events-table'],
  classNameBindings: ['showScrollMessage']
});

export default connect(stateToComputed)(EventsTableContainer);
