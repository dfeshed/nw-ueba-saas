import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { isArrowLeft, isBackspace, isEnter, isEscape } from 'investigate-events/util/keys';
import { next, scheduleOnce } from '@ember/runloop';

const { log } = console;// eslint-disable-line no-unused-vars

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

  didUpdateAttrs() {
    this._super(...arguments);
    if (this.get('isActive')) {
      // We schedule this after render to give time for the input to
      // be rendered before trying to focus on it.
      scheduleOnce('afterRender', this, '_focusOnInput');
    }
  },

  focusOut() {
    const input = this.element.querySelector('input');
    // If this component looses focus while there is a value, we need to save
    // it off so that the inactive state renders properly.
    if (input && input.value != '') {
      this._broadcast(MESSAGE_TYPES.VALUE_SET, input.value);
    }
  },

  actions: {
    /**
     * Handle keyboard interactions.
     * @param {string} input The value from the DOM input element
     * @param {Object} event A KeyboardEvent
     * @private
     */
    onKeyDown(input, event) {
      input = input || '';// guard against undefined or null
      if (isBackspace(event) && input.length === 0) {
        next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_BACKSPACE_KEY));
      } else if (isEnter(event) && !this._isInputEmpty(input)) {
        this._broadcast(MESSAGE_TYPES.VALUE_ENTER_KEY, input);
      } else if (isEscape(event)) {
        this._broadcast(MESSAGE_TYPES.VALUE_ESCAPE_KEY);
      } else if (isArrowLeft(event) && event.target.selectionStart === 0) {
        next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY, input));
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

  _focusOnInput() {
    const input = this.element.querySelector('input');
    if (input) {
      input.focus();
      input.setSelectionRange(0, 0);
    }
  },

  _isInputEmpty: (input) => {
    const trimmedInput = input.trim();
    const isEmpty = trimmedInput.length === 0;
    const hasEmptyQuotes = trimmedInput.match(/^['"]\s*['"]$|^['"]$/);
    return isEmpty || (hasEmptyQuotes && hasEmptyQuotes.length > 0);
  }
});