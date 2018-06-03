import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import {
  hasError,
  errorMessage
} from 'investigate-process-analysis/reducers/process-tree/selectors';

const stateToComputed = (state) => ({
  hasError: hasError(state),
  errorMessage: errorMessage(state)
});

const EventsTableComponent = Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['process-events-table']
});
export default connect(stateToComputed)(EventsTableComponent);
