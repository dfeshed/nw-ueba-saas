import Component from '@ember/component';
import { next, scheduleOnce } from '@ember/runloop';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';
import { isArrowLeft, isBackspace, isEscape } from 'investigate-events/util/keys';
import { escapeBackslash, escapeSingleQuotes, properlyQuoted, stripOuterSingleQuotes } from 'investigate-events/util/quote';
import { complexOperators } from 'investigate-events/actions/utils';
import * as MESSAGE_TYPES from '../message-types';
import Ember from 'ember';

const { log } = console;// eslint-disable-line no-unused-vars

const QUERY_PILL = 'Query Filter';
const FREE_FORM_PILL = 'Free-Form Filter';
// This is used for an internal Ember API function: escapeExpression
const { Handlebars: { Utils } } = Ember;

export default Component.extend({
  classNameBindings: ['isPopulated', ':pill-value'],

  /**
   * Whether or not to send the next focus out event that occurs
   * @type {boolean}
   * @public
   */
  swallowNextFocusOut: false,

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
   * Should we automatically focus on the power-select input
   * @private
   */
  _isAutoFocus: true,

  /**
   * The options used for the power-select. They are grouped to provide a way to
   * have 2 "lists". One list is the default type of pill to create, the second
   * list is the advanced options (creating free-form or text pills). The first
   * group's name is hidden via CSS, but we still need to give it a name so it
   * renders. That's why it's a space character.
   * @private
   */
  _options: [
    { groupName: ' ', options: [QUERY_PILL] },
    { groupName: 'Advanced Options', options: [ FREE_FORM_PILL ] }
  ],

  /**
   * The value typed into power-selects input
   * @private
   */
  _searchString: null,

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
        // For text values with html tags, we will need to encode the string,
        // before passing it through htmlSafe so that the original value is retained.
        // This is an internal Ember API function
        const transformedString = Utils.escapeExpression(match[1]);
        ret = htmlSafe(`
          <span class="quote-highlight">'</span>
          ${transformedString}
          <span class="quote-highlight">'</span>
        `);
      }
    }
    return ret;
  },

  /**
   * Indicates if something is being rendered by this template and that it isn't
   * empty. Controls whether padding/spacing is required
   * @private
   */
  @computed('valueString', 'isActive')
  isPopulated(valueString, isActive) {
    return (!!valueString && valueString.length > 0) || isActive;
  },

  didUpdateAttrs() {
    this._super(...arguments);
    if (this.get('isActive') && this.get('_isAutoFocus')) {
      // We schedule this after render to give time for the input to
      // be rendered before trying to focus on it.
      scheduleOnce('afterRender', this, '_focusOnPowerSelectTrigger');
    }
  },

  click() {
    // If this component is not active and the user clicks on it, dispatch an
    // action so that the parent can coordinate the activation of this component.
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.VALUE_CLICKED);
    }
  },

  actions: {
    onBlur(powerSelectAPI) {
      const { lastSearchedText, searchText } = powerSelectAPI;
      // If this component looses focus while there is a value, we need to save
      // it off so that the inactive state renders properly.
      if (searchText !== null && searchText !== '') {
        this.set('_isAutoFocus', false);
        this._broadcast(MESSAGE_TYPES.VALUE_SET, searchText);
        // force text back into view
        const el = this.element.querySelector('input');
        if (el) {
          el.value = lastSearchedText;
        }
      }
    },

    onFocus(powerSelectAPI) {
      const { actions } = powerSelectAPI;
      const valueString = this.get('valueString') || '';
      const trimmedValueString = stripOuterSingleQuotes(valueString).trim();
      if (trimmedValueString !== '') {
        // force text back into view
        const el = this.element.querySelector('input');
        if (el) {
          el.value = trimmedValueString;
          this.set('_searchString', trimmedValueString);
        }
        actions.search(trimmedValueString);
        actions.open();
      }
    },

    onInput(input, powerSelectAPI) {
      this.set('_searchString', input);
      const match = complexOperators.find((d) => input.includes(d));
      const option = (match) ? FREE_FORM_PILL : QUERY_PILL;
      next(this, () => powerSelectAPI.actions.highlight(option));
    },

    /**
     * This function is called every time a key is pressed, and is invoked
     * before power-select reacts to the key that was pressed. As a side note,
     * we cannot combine `onInput`'s functionality here because this code runs
     * before any down-selection of options happens.
     * @private
     */
    onKeyDown(powerSelectAPI, event) {
      if (isEscape(event)) {
        this._broadcast(MESSAGE_TYPES.VALUE_ESCAPE_KEY);
      } else if (isBackspace(event) && event.target.value === '') {
        next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_BACKSPACE_KEY));
      } else if (isArrowLeft(event) && event.target.selectionStart === 0) {
        const { value } = event.target;
        next(this, () => this._broadcast(MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY, value));
      }
    },

    /*
     * This function is called when you press ENTER. It's effectively the same
     * as clicking on the 'Query Pill` option
     */
    onChange(selection, powerSelectAPI/* event */) {
      if (selection !== null) {
        const { actions, searchText } = powerSelectAPI;
        const trimmedInput = stripOuterSingleQuotes(searchText).trim();
        const value = escapeSingleQuotes(escapeBackslash(trimmedInput));
        // cleanup
        this.set('_searchString', undefined);
        this.set('_isAutoFocus', true);
        actions.search('');
        // send event
        if (selection === QUERY_PILL && !this._isInputEmpty(value)) {
          this._broadcast(MESSAGE_TYPES.VALUE_ENTER_KEY, value);
        } else if (selection === FREE_FORM_PILL) {
          // _debugContainerKey is a private Ember property that returns the full
          // component name (component:query-container/pill-value).
          const [ , source ] = this._debugContainerKey.split('/');
          // send value up to create a complex pill
          this._broadcast(MESSAGE_TYPES.CREATE_FREE_FORM_PILL, [value, source]);
        }
      }
    },

    onOptionMouseDown() {
      this.set('swallowNextFocusOut', true);
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

  _focusOnPowerSelectTrigger() {
    const trigger = this.element.querySelector('.pill-value input');
    if (trigger) {
      trigger.focus();
      if (this.get('isFocusAtBeginning')) {
        // Set the cursor position on the next run loop.
        next(this, () => {
          trigger.setSelectionRange(0, 0);
          this.set('isFocusAtBeginning', false);
        });
      }
    }
  },

  _isInputEmpty: (input) => {
    const trimmedInput = input.trim();
    const isEmpty = trimmedInput.length === 0;
    const hasEmptyQuotes = trimmedInput.match(/^['"]\s*['"]$/);
    return isEmpty || (hasEmptyQuotes && hasEmptyQuotes.length > 0);
  },

  /**
   * Function used by EPS to down-select options given what's typed into the
   * search box. Since we don't want it to actually change, this function
   * just returns `true`.
   * @private
   */
  _matcher: () => true,

  /**
   * Used by power-select to position the dropdown.
   * @private
   */
  _calculatePosition: (trigger, dropdown) => {
    const { innerWidth } = window;
    const { offsetWidth } = dropdown;
    const pill = trigger.closest('.query-pill');
    const { top, left } = pill ? pill.getBoundingClientRect() : { top: 0, left: 0 };
    const rightEdge = left + offsetWidth;
    const offset = (rightEdge > innerWidth) ? rightEdge - innerWidth + 14 : 5;
    const style = {
      top: top + 34,
      left: Math.max(0, left - offset)
    };
    return {
      horizontalPosition: 'auto',
      verticalPosition: 'auto',
      style
    };
  }
});