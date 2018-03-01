import Component from '@ember/component';
import { connect } from 'ember-redux';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';

const stateToComputed = (state) => ({
  count: state.investigate.eventCount.data,
  isAtThreshold: resultCountAtThreshold(state)
});

const EventCounter = Component.extend({
  // Choosing whether or not to render anything at all
  // with this component rather than the parent component
  tagName: ''
});

export default connect(stateToComputed)(EventCounter);
