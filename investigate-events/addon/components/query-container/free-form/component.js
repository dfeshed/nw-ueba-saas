import Component from '@ember/component';
import { connect } from 'ember-redux';
import { debounce, scheduleOnce, throttle } from '@ember/runloop';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';
import { freeFormText, hasRequiredValuesToQuery } from 'investigate-events/reducers/investigate/query-node/selectors';
import { addFreeFormFilter, deleteAllGuidedPills, updatedFreeFormText } from 'investigate-events/actions/guided-creators';

const stateToComputed = (state) => ({
  freeFormText: freeFormText(state),
  hasRequiredValuesToQuery: hasRequiredValuesToQuery(state)
});

const dispatchToActions = {
  addFreeFormFilter,
  deleteAllGuidedPills,
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
    const freeFormText = event.target.value.trim();
    // Don't do anything if there is nothing in the box
    if (freeFormText.length > 0) {

      // Don't do anything if the text is the same as it was
      // when the component was initially rendered
      if (this.get('initialFreeFormText') !== freeFormText) {
        const pills = transformTextToPillData(freeFormText, undefined, false, true);
        pills.forEach((pillData, i) => {
          this.send('addFreeFormFilter', {
            pillData,
            position: i,
            shouldAddFocusToNewPill: false,
            // Only the first pill should have `fromFreeFormMode` set because it
            // causes all other pills to be deleted. Setting it on all of them
            // means only the last pill actually gets added.
            fromFreeFormMode: i === 0
          });
        });
      }
    } else if (freeFormText.length === 0 && this.get('freeFormText').length !== 0) {
      // nuke pills data. Pills have been deleted through FF
      this.send('deleteAllGuidedPills');
    }
  },

  debouncedKeyUp({ event }) {
    // Update text in state because if it is different than the text already in
    // state, need to treat query as dirty.
    this.send('updatedFreeFormText', event.target.value);
  },

  actions: {
    keyDown(value, e) {
      if (e.keyCode === 13 && this.get('hasRequiredValuesToQuery')) {
        e.target.blur();
        this.throttledFocusOut(e);
        this.executeQuery();
      }
    },

    keyUp(value, e) {
      debounce(this, this.debouncedKeyUp, { event: e }, 100);
    },

    focusOut(value, e) {
      this.throttledFocusOut(e);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(freeForm);
