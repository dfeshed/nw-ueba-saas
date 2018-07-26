import Component from '@ember/component';
import { connect } from 'ember-redux';
import { run } from '@ember/runloop';

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

  actions: {
    changeView(view) {
      this.send('setQueryView', view);
      run.next(() => {
        if (this.get('isOnFreeForm')) {
          this.$('.rsa-investigate-free-form-query-bar input').focus();
        } else if (this.get('isOnGuided')) {
          this.$('.new-pill-template .pill-meta input').focus();
        }
      });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryBar);
