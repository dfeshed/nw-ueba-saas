import Component from '@ember/component';
import { connect } from 'ember-redux';
import { throttle, debounce, scheduleOnce } from '@ember/runloop';

import { hasRequiredValuesToQuery, freeFormText } from 'investigate-events/reducers/investigate/query-node/selectors';
import { addFreeFormFilter, updatedFreeFormText } from 'investigate-events/actions/guided-creators';

const stateToComputed = (state) => ({
  freeFormText: freeFormText(state),
  hasRequiredValuesToQuery: hasRequiredValuesToQuery(state)
});

const dispatchToActions = {
  addFreeFormFilter,
  updatedFreeFormText
};

const freeForm = Component.extend({
  classNames: ['rsa-investigate-free-form-query-bar'],

  initialFreeFormText: null,

  init() {
    this._super(...arguments);
    scheduleOnce('afterRender', this, () => {
      this.set('initialFreeFormText', this.get('freeFormText'));
    });
    if (this.get('takeFocus')) {
      // Schedule after render so that thing that needs
      // focus is actually there
      scheduleOnce('afterRender', this, () => {
        this.element.querySelector('input').focus();
      });
    }
  },

  throttledFocusOut(e) {
    // Don't let multiple focus out calls through,
    // they would be identical
    throttle(this, this.onFocusOut, { event: e }, 250);
  },

  onFocusOut({ event }) {
    const freeFormText = event.target.value;
    // Don't do anything if there is nothing in the box
    if (freeFormText.length > 0) {

      // Don't do anything if the text is the same as it was
      // when the component was initially rendered
      if (this.get('initialFreeFormText') !== freeFormText) {
        this.send('addFreeFormFilter', { freeFormText });
      }
    }
  },

  debouncedKeyUp({ event }) {
    // Update text in state because if it is different
    // than the text already in state, need to treat query
    // as dirty
    this.send('updatedFreeFormText', event.target.value);
  },

  actions: {
    keyDown(e) {
      if (e.keyCode === 13 && this.get('hasRequiredValuesToQuery')) {
        e.target.blur();
        this.throttledFocusOut(e);
        this.executeQuery();
      }
    },

    keyUp(e) {
      debounce(this, this.debouncedKeyUp, { event: e }, 100);
    },

    focusOut(e) {
      this.throttledFocusOut(e);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(freeForm);