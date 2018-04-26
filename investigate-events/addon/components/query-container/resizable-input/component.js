/**
 * This component was copied from:
 * https://github.com/cibernox/ember-power-select-typeahead/blob/master/addon/components/power-select-typeahead/trigger.js
 * I added stuff to dynamically resize the input based upon the number of
 * characters typed in the input.
 * @public
 */
import Component from '@ember/component';
import { computed } from '@ember/object';
import { isBlank } from '@ember/utils';
import { run } from '@ember/runloop';

export default Component.extend({
  bindClassNames: ['has-selection:select'],
  tagName: '',
  value: '',
  valueLength: 1,

  _calculateSize(value) {
    const length = (value && value.length) ? value.length : 1;
    this.set('valueLength', length);
  },

  /**
   * value for input
   * @private
   */
  text: computed('select.selected', 'extra.labelPath', {
    get() {
      return this.getSelectedAsText();
    },
    set(_, v) {
      return v;
    }
  }),

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
    // if no selection on init
    if (!oldSelect) {
      return;
    }
    /*
     * We need to update the input field with value of the selected option
     * whenever we're closing the select box.
     */
    if (oldSelect.isOpen && !newSelect.isOpen && newSelect.searchText) {
      const input = document.querySelector(`#ember-power-select-typeahead-input-${newSelect.uniqueId}`);
      const newText = this.getSelectedAsText();
      if (input.value !== newText) {
        input.value = newText;
        this._calculateSize(input.value);
      }
      this.set('text', newText);
    }

    if (newSelect.lastSearchedText !== oldSelect.lastSearchedText) {
      if (isBlank(newSelect.lastSearchedText)) {
        run.schedule('actions', null, newSelect.actions.close, null, true);
      } else {
        run.schedule('actions', null, newSelect.actions.open);
      }
    }
  },

  actions: {
    /**
     * on mousedown prevent propagation of event
     *
     * @private
     * @method stopPropagation
     * @param {Object} event
     */
    stopPropagation(e) {
      e.stopPropagation();
    },

    /**
     * called from power-select internals
     *
     * @private
     * @method handleKeydown
     * @param {Object} event
     */
    handleKeydown(e) {
      // up or down arrow and if not open, no-op and prevent parent handlers
      // from being notified
      if ([38, 40].indexOf(e.keyCode) > -1 && !this.get('select.isOpen')) {
        e.stopPropagation();
        return;
      }
      const isLetter = e.keyCode >= 48 && e.keyCode <= 90 || e.keyCode === 32; // Keys 0-9, a-z or SPACE
      // if isLetter, escape or enter, prevent parent handlers from being notified
      if (isLetter || [13, 27].indexOf(e.keyCode) > -1) {
        const select = this.get('select');
        // open if loading msg configured
        if (!select.isOpen && this.get('loadingMessage')) {
          run.schedule('actions', null, select.actions.open);
        }
        // +1 because this function is called before the <input/> registers
        // the key that was pressed.
        this._calculateSize(e.target.value + 1);
        e.stopPropagation();
      }

      // optional, passed from power-select
      const onkeydown = this.get('onKeydown');
      if (onkeydown && onkeydown(e) === false) {
        return false;
      }
    }
  },

  /**
   * obtains seleted value based on complex object or primitive value from power-select publicAPI
   *
   * @private
   * @method getSelectedAsText
   */
  getSelectedAsText() {
    const labelPath = this.get('extra.labelPath');
    let value;
    if (labelPath) {
      // complex object
      value = this.get(`select.selected.${labelPath}`);
    } else {
      // primitive value
      value = this.get('select.selected');
    }
    if (value === undefined) {
      value = '';
    }
    return value;
  }
});