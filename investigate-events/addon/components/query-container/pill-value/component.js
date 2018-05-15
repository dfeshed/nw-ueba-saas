import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';

// const { log } = console;

export default Component.extend({
  classNameBindings: ['isActive', ':pill-value'],

  /**
   * Does this component currently have focus?
   * @type {boolean}
   * @public
   */
  isActive: false,
  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},
  /**
   * The value to display.
   * @type {string}
   * @public
   */
  valueString: null,

  didRender() {
    const input = this.element.querySelector('input');
    if (input) {
      input.focus();
    }
  },

  focusOut() {
    const input = this.element.querySelector('input');
    if (input) {
      this._broadcast(MESSAGE_TYPES.VALUE_SET, input.value);
    }
  },

  actions: {
    /**
     * Why the keydown event? Because the power-select components use keydown to
     * handle keyboard interactions. This can cause situations where you press a
     * key when the operator has focus, but the for some seemingly unknown
     * reason, this component reacts. Using keydown has little consequence
     * except for BACKSPACE handling. We need to manually trim the last
     * character befor send the event since it hasn't been removed from the
     * input yet.
     * @param {Object} event A KeyboardEvent
     * @private
     */
    onKeyDown(input, event) {
      input = input || '';// guard against undefined or null
      // 'keyCode' is deprecated in favor of 'key', but the Ember test-helpers
      // don't support 'key'
      switch (event.keyCode) {
        case 8: { // Backspace
          const _input = input.length > 0 ? input.slice(0, -1) : input;
          this._broadcast(MESSAGE_TYPES.VALUE_BACKSPACE_KEY, _input);
          break;
        }
        case 13: // Enter
          if (!this._isInputEmpty(input)) {
            this._broadcast(MESSAGE_TYPES.VALUE_ENTER_KEY, input);
          }
          break;
        case 27: // Escape
          this._broadcast(MESSAGE_TYPES.VALUE_ESCAPE_KEY, input);
          break;
        case 37: // ArrowLeft
          this._broadcast(MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY, input);
          break;
        case 39: // ArrowRight
          this._broadcast(MESSAGE_TYPES.VALUE_ARROW_RIGHT_KEY, input);
          break;
      }
    }
  },

  // ************************************************************************ //
  //                          PRIVATE FUNCTIONS                               //
  // ************************************************************************ //
  /**
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
   * @param {Object} data The event data
   * @private
   */
  _broadcast(type, data) {
    this.get('sendMessage')(type, data);
  },

  _isInputEmpty: (input) => {
    const trimmedInput = input.trim();
    const isEmpty = trimmedInput.length === 0;
    const hasEmptyQuotes = trimmedInput.match(/^['"]\s*['"]$|^['"]$/);
    return isEmpty || (hasEmptyQuotes && hasEmptyQuotes.length > 0);
  }
});
