import Component from '@ember/component';
import { connect } from 'ember-redux';

import { setQueryView } from 'investigate-events/actions/interaction-creators';
import { isOnFreeForm, isOnGuided } from 'investigate-events/reducers/investigate/query-node/selectors';
import { hasError, hasWarning, hasOfflineServices } from 'investigate-events/reducers/investigate/query-stats/selectors';

const dispatchToActions = {
  setQueryView
};

const stateToComputed = (state) => ({
  isOnFreeForm: isOnFreeForm(state),
  isOnGuided: isOnGuided(state),
  queryView: state.investigate.queryNode.queryView,
  isConsoleOpen: state.investigate.queryStats.isConsoleOpen,
  hasOfflineServices: hasOfflineServices(state),
  consoleHasError: hasError(state),
  consoleHasWarning: hasWarning(state)
});

const QueryBar = Component.extend({
  classNames: ['query-bar-selection'],
  classNameBindings: ['queryView', 'isConsoleOpen', 'consoleHasError', 'consoleHasWarning', 'hasOfflineServices'],

  // Whether or not child views should take
  // focus when they are rendered
  takeFocus: false,

  actions: {
    changeView(view) {
      this.send('setQueryView', view);

      // after we have changed the view once,
      // child views should take focus
      this.set('takeFocus', true);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryBar);
