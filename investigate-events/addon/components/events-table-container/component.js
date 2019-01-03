import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  percentageOfEventsDataReturned,
  areEventsStreaming
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { hasColumnGroups } from 'investigate-events/reducers/investigate/data-selectors';

const stateToComputed = (state) => ({
  areEventsStreaming: areEventsStreaming(state),
  hasColumnGroups: hasColumnGroups(state),
  percent: percentageOfEventsDataReturned(state)
});

const EventsTableContainer = Component.extend({
  classNames: ['rsa-investigate-events-table'],
  classNameBindings: ['showScrollMessage']
});

export default connect(stateToComputed)(EventsTableContainer);
