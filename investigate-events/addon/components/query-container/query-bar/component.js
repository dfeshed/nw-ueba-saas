import Component from '@ember/component';
import { connect } from 'ember-redux';

import { hasError, hasWarning, hasOfflineServices } from 'investigate-events/reducers/investigate/query-stats/selectors';

const stateToComputed = (state) => ({
  isConsoleOpen: state.investigate.queryStats.isConsoleOpen,
  hasOfflineServices: hasOfflineServices(state),
  consoleHasError: hasError(state),
  consoleHasWarning: hasWarning(state)
});

const QueryBar = Component.extend({
  classNames: ['query-bar-selection'],
  classNameBindings: ['isConsoleOpen', 'consoleHasError', 'consoleHasWarning', 'hasOfflineServices'],

  // Whether or not child views should take
  // focus when they are rendered
  takeFocus: false
});

export default connect(stateToComputed)(QueryBar);
