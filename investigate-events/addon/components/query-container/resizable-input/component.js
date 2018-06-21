/**
 * This component was copied from:
 * https://github.com/cibernox/ember-power-select-typeahead/blob/master/addon/components/power-select-typeahead/trigger.js
 * I added stuff to dynamically resize the input based upon the number of
 * characters typed in the input. If this isn't sufficient, take a look at
 * https://github.com/cibernox/ember-text-measurer.
 * @public
 */
import Component from '@ember/component';
import { computed } from '@ember/object';

const { log } = console;// eslint-disable-line no-unused-vars

export default Component.extend({
  bindClassNames: ['has-selection:select'],
  tagName: '',
  value: '',

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
   * Value for input's "size" attribute
   * @private
   */
  size: computed('text', function() {
    let _size = 0;
    return {
      get() {
        const selectedTextLength = this.get('text').length || 1;
        return _size > 0 ? _size : selectedTextLength;
      },
      set(key, value) {
        _size = value;
        return value;
      }
    };
  }()),

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
    // We need to update the input field with value of the selected option
    // whenever we're closing the select box.
    if (oldSelect.isOpen && !newSelect.isOpen && newSelect.searchText) {
      const input = document.querySelector(`#ember-power-select-typeahead-input-${newSelect.uniqueId}`);
      const newText = this.getSelectedAsText();
      if (input.value !== newText) {
        input.value = newText;
      }
      this.set('text', newText);
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
     * Called from power-select internals. Sets `size` to length of the typed
     * text, or `1`.
     * @param {Object} event
     * @private
     */
    handleInput(e) {
      this.set('size', e.target.value.length || 1);
      const oninput = this.get('onInput');
      if (oninput) {
        oninput(e);
      }
    },

    /**
     * Called from power-select internals. Sets `size` back to default value of
     * `0`.
     * @param {Object} event
     * @private
     */
    handleFocus(e) {
      this.set('size', 0);
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
      this.set('size', this.get('text').length);
      const onblur = this.get('onBlur');
      if (onblur && onblur(e) === false) {
        return false;
      }
    }
  },

  /**
   * obtains seleted value based on complex object or primitive value from
   * power-select publicAPI
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