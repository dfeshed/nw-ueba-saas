import Component from '@ember/component';
import { connect } from 'ember-redux';

import { toggleQueryConsole } from 'investigate-events/actions/interaction-creators';
import { isConsoleEmpty, hasError, hasWarning } from 'investigate-events/reducers/investigate/query-stats/selectors';

const dispatchToActions = {
  toggleQueryConsole
};

const stateToComputed = (state) => ({
  description: state.investigate.queryStats.description,
  isDisabled: isConsoleEmpty(state),
  isOpen: state.investigate.queryStats.isConsoleOpen,
  hasError: hasError(state),
  hasWarning: hasWarning(state)
});

const ConsoleTrigger = Component.extend({
  classNames: ['console-trigger'],
  classNameBindings: [
    'isDisabled',
    'isOpen',
    'hasError',
    'hasWarning'
  ]
});

export default connect(stateToComputed, dispatchToActions)(ConsoleTrigger);
