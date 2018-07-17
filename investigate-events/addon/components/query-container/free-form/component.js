import Component from '@ember/component';
import { connect } from 'ember-redux';
import { throttle } from '@ember/runloop';

import { dirtyQueryToggle } from 'investigate-events/actions/query-validation-creators';
import { hasRequiredValuesToQuery } from 'investigate-events/reducers/investigate/query-node/selectors';
import { freeFormText } from 'investigate-events/reducers/investigate/next-gen/selectors';
import { addFreeFormFilter } from 'investigate-events/actions/next-gen-creators';

const stateToComputed = (state) => ({
  freeFormText: freeFormText(state),
  hasRequiredValuesToQuery: hasRequiredValuesToQuery(state)
});

const dispatchToActions = {
  dirtyQueryToggle,
  addFreeFormFilter
};

const freeForm = Component.extend({
  classNames: ['rsa-investigate-free-form-query-bar'],

  initialFreeFormText: null,

  init() {
    this._super(...arguments);
    this.set('initialFreeFormText', this.get('freeFormText'));
  },

  throttledFocusOut(e) {
    // Don't let multiple focus out calls through,
    // they would be identical
    this.set('event', e);
    throttle(this, this.onFocusOut, 250);
  },

  onFocusOut() {
    const text = this.get('event').target.value;
    // Don't do anything if there is nothing in the box
    if (text.length > 0) {

      // Don't do anything if the text is the same as it was
      // when the component was initially rendered
      if (this.get('initialFreeFormText') !== text) {
        this.send('addFreeFormFilter', text);
      }
    }
  },

  actions: {
    keyDown(e) {
      this.send('dirtyQueryToggle');
      if (this.get('hasRequiredValuesToQuery')) {
        if (e.keyCode === 13) {
          e.target.blur();
          this.throttledFocusOut(e);
          this.executeQuery();
        }
      }
    },

    focusOut(e) {
      this.throttledFocusOut(e);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(freeForm);