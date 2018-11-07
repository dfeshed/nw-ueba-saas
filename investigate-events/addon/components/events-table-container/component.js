import Component from '@ember/component';
import { connect } from 'ember-redux';
import { percentageOfEventsDataReturned } from 'investigate-events/reducers/investigate/event-results/selectors';
import { hasColumnGroups } from 'investigate-events/reducers/investigate/data-selectors';

const stateToComputed = (state) => ({
  status: state.investigate.eventResults.status,
  hasColumnGroups: hasColumnGroups(state),
  percent: percentageOfEventsDataReturned(state)
});

const EventsTableContainer = Component.extend({
  classNames: ['rsa-investigate-events-table'],
  classNameBindings: ['showScrollMessage']
});

export default connect(stateToComputed)(EventsTableContainer);
