import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { next, scheduleOnce } from '@ember/runloop';
import * as MESSAGE_TYPES from '../message-types';
import { isArrowLeft, isBackspace, isEnter, isEscape } from 'investigate-events/util/keys';
import { htmlSafe } from '@ember/string';
import { properlyQuoted } from 'investigate-events/util/quote';

const { log } = console;// eslint-disable-line no-unused-vars

export default Component.extend({
  classNameBindings: ['isPopulated', ':pill-value'],

  /**
   * Does this component currently have focus?
   * @type {boolean}
   * @public
   */
  isActive: false,

  /**
   * Should we position the cursor at the beginning of the string when focusing
   * on this component?
   * @type {boolean}
   * @public
   */
  isFocusAtBeginning: false,

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

  /**
   * Prepares `valueString` for display in the template. If the incoming value
   * is single quoted, it will replace the wrapping single quotes with styled
   * single quotes. This helps the user to differentiate the wrapping quotes
   * from quotes within the string.
   * @private
   */
  @computed('valueString')
  valueDisplay(valueString) {
    let ret = valueString;
    if (typeof(valueString) === 'string') {
      const match = valueString.match(properlyQuoted);
      if (match) {
        ret = htmlSafe(`<span class="quote-highlight">'</span>${match[1]}<span class="quote-highlight">'</span>`);
      }
    }
    return ret;
  },

  // Indicates if something is being rendered by this template
  // and that it isn't empty. Controls whether padding/spacing is
  // required
  @computed('valueString', 'isActive')
  isPopulated(valueString, isActive) {
    return (!!valueString && valueString.length > 0) || isActive;
  },

  didUpdateAttrs() {
    this._super(...arguments);
    if (this.get('isActive')) {
      // We schedule this after render to give time for the input to
      // be rendered before trying to focus on it.
      scheduleOnce('afterRender', this, '_focusOnInput');
    }
  },

  click() {
    // If this component is not active and the user clicks on it, dispatch an
    // action so that the parent can coordinate the activation of this component.
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.VALUE_CLICKED);
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
      if (this.get('isFocusAtBeginning')) {
        input.setSelectionRange(0, 0);
        this.set('isFocusAtBeginning', false);
      }
    }
  },

  _isInputEmpty: (input) => {
    const trimmedInput = input.trim();
    const isEmpty = trimmedInput.length === 0;
    const hasEmptyQuotes = trimmedInput.match(/^['"]\s*['"]$/);
    return isEmpty || (hasEmptyQuotes && hasEmptyQuotes.length > 0);
  }
});