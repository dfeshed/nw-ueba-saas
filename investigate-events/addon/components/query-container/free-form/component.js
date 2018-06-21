import Component from '@ember/component';
import { dirtyQueryToggle } from 'investigate-events/actions/query-validation-creators';
import { hasRequiredValuesToQuery } from 'investigate-events/reducers/investigate/query-node/selectors';
import { connect } from 'ember-redux';

const stateToComputed = (state) => ({
  hasRequiredValuesToQuery: hasRequiredValuesToQuery(state)
});

const dispatchToActions = {
  dirtyQueryToggle
};

const freeForm = Component.extend({
  classNames: ['rsa-investigate-free-form-query-bar'],

  actions: {
    keyDown(e) {
      this.send('dirtyQueryToggle');
      if (this.get('hasRequiredValuesToQuery')) {
        if (e.keyCode === 13) {
          e.target.blur();
          this.executeQuery(this.get('filters'));
        }
      }
    },

    focusOut(e) {
      // send action to set filters
      this.addFilters(e.target.value);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(freeForm);