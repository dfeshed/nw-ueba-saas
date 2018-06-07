import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';
import { isArrowLeft, isBackspace, isEnter, isEscape } from 'investigate-events/util/keys';

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
      if (isBackspace(event) && input.length === 0) {
        this._broadcast(MESSAGE_TYPES.VALUE_BACKSPACE_KEY);
      } else if (isEnter(event) && !this._isInputEmpty(input)) {
        this._broadcast(MESSAGE_TYPES.VALUE_ENTER_KEY, input);
      } else if (isEscape(event)) {
        this._broadcast(MESSAGE_TYPES.VALUE_ESCAPE_KEY, input);
      } else if (isArrowLeft(event)) {
        // Why are we handling ARROW_LEFT on KEYUP and KEYDOWN? See note below.
        this.set('atLeftEdge', event.target.selectionStart === 0);
      }
    },
    onKeyUp(input, event) {
      if (isArrowLeft(event)) {
        // Why are we handling ARROW_LEFT on KEYUP and KEYDOWN? See note below.
        if (this.get('atLeftEdge')) {
          this._broadcast(MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY);
        }
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

// NOTES
/*
 * So, why are we handling key-up and key-down for the ARROW_LEFT key? It's a
 * bit of an event dance. Let's say we have the following string in the input:
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