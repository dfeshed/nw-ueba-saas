import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { isArrowLeft, isBackspace, isEnter, isEscape } from 'investigate-events/util/keys';
import { next, scheduleOnce } from '@ember/runloop';

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
     * Why the keydown event? Because the power-select components use keydown to
     * handle keyboard interactions. This can cause situations where you press a
     * key when this has focus, but another component reacts. To combat this for
     * certain key types, we delay processing the key press until `onKeyUp`.
     * See note at bottom of file.
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
        this._broadcast(MESSAGE_TYPES.VALUE_ESCAPE_KEY, input);
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

// NOTES
/*
 * So, why are we handling key-up and key-down for the certain keys? It's a bit
 * of an event dance. Let's say we have the following string in the input:
 *
 * "bar"
 *
 * The cursor is at the right of the "r". When you press the ARROW_LEFT key, the
 * series of events goes something like this:
 * 1. key-down (cursor index 3)
 * 2. The cursor moves to the left
 * 3. key-up (cursor index 2)
 *
 * To determine when we need to move control to the operator, we need to know
 * when the cursor is at the left edge and the ARROW_LEFT key is pressed.
 * If we just use the key-down event to move control, this is what would happen
 * if the cursor was already at the left edge:
 * 1. key-down (cursor index 0)
 * 2. event dispatched, control moves to operator
 * 3. key-up (moves cursor 1 to the left on operator)
 *
 * It moves the cursor in the operator control because that input handles the
 * key-up. Not what we want. To prevent this, we need to handle moving control
 * to the operator from a key-up (the last in the key-* events that happen when
 * pressing a key). We can't just use cursor position = 0 at key-up because it
 * would move control to the operator when moving the cursor from the right
 * side of "b" to its left side, and that jump would feel awkward.
 *
 * So, we determine if we're at the left edge in the key-down (cursor index 0),
 * and dispatch the event in the key-up.
 */