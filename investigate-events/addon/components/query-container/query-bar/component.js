import Component from '@ember/component';
import { connect } from 'ember-redux';

import { setQueryView } from 'investigate-events/actions/interaction-creators';
import { isOnFreeForm, isOnGuided } from 'investigate-events/reducers/investigate/query-node/selectors';

const dispatchToActions = {
  setQueryView
};

const stateToComputed = (state) => ({
  isOnFreeForm: isOnFreeForm(state),
  isOnGuided: isOnGuided(state),
  queryView: state.investigate.queryNode.queryView
});

const QueryBar = Component.extend({
  classNames: ['query-bar-selection'],
  classNameBindings: ['queryView'],

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
