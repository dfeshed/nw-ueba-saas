/**
 * This component was copied from:
 * https://github.com/cibernox/ember-power-select-typeahead/blob/master/addon/components/power-select-typeahead/trigger.js
 * I added stuff to dynamically resize the input based upon the number of
 * characters typed in the input. If this isn't sufficient, take a look at
 * https://github.com/cibernox/ember-text-measurer.
 * @public
 */
import Component from '@ember/component';

const { log } = console;// eslint-disable-line no-unused-vars

export default Component.extend({
  bindClassNames: ['has-selection:select'],
  tagName: '',
  value: '',
  text: '',

  /**
   * Lifecycle Hook
   * power-select updates the state of the publicAPI (select) for every
   * typeahead so we capture this as `state` via oldSelect && newSelect.
   * @private
   */
  didReceiveAttrs() {
    this._super(...arguments);
    const oldSelect = this.get('oldSelect');
    const newSelect = this.set('oldSelect', this.get('select'));
    if (!oldSelect) {
      return;
    }
    if (oldSelect.searchText !== newSelect.searchText) {
      this.set('text', newSelect.searchText);
    }
  },

  actions: {
    /**
     * On mousedown prevent propagation of event
     * @param {Object} event
     * @private
     */
    stopPropagation(e) {
      e.stopPropagation();
    },

    /**
     * Called from power-select internals
     * @param {Object} event
     * @private
     */
    handleKeydown(e) {
      e.stopPropagation();
      // optional, passed from power-select
      const onkeydown = this.get('onKeydown');
      if (onkeydown && onkeydown(e) === false) {
        return false;
      }
    },

    /**
     * Called from power-select internals. Set `text` so that the shadow SPAN
     * resizes.
     * @param {Object} event
     * @private
     */
    handleInput(e) {
      this.set('text', e.target.value);
      const oninput = this.get('onInput');
      if (oninput) {
        oninput(e);
      }
    },

    /**
     * Called from power-select internals.
     * @param {Object} event
     * @private
     */
    handleFocus(e) {
      const onfocus = this.get('onFocus');
      if (onfocus && onfocus(e) === false) {
        return false;
      }
    },

    /**
     * Called from power-select internals. Sets `size` back to what was
     * previously selected as losing focus will not set a new value, so the old
     * value is still correct.
     * @param {Object} event
     * @private
     */
    handleBlur(e) {
      const onblur = this.get('onBlur');
      if (onblur && onblur(e) === false) {
        return false;
      }
    }
  }
});
