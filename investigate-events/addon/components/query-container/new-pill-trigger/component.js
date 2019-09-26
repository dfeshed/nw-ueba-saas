import Component from '@ember/component';

import * as MESSAGE_TYPES from '../message-types';

export default Component.extend({
  classNames: ['new-pill-trigger-container'],

  /**
   * Are all Core Services at a revision that allows Text searching to be
   * performed? Passed to subcomponents, not used directly.
   * @type {boolean}
   * @public
   */
  canPerformTextSearch: true,

  /**
   * Whether or not the new pill trigger is in new pill mode or trigger mode
   * @type {boolean}
   * @public
   */
  isAddNewPill: false,

  /**
   * Where a new pill would be in the list of pills if it was added
   * @type {number}
   * @public
   */
  newPillPosition: null,

  /**
   * List of meta provided from above and simply
   * passed through to meta component
   * @type {Object}
   * @public
   */
  metaOptions: null,

  /**
   * Object with keys `language` and `aliases`
   */
  languageAndAliasesForParser: null,

  /**
   * Placeholder text
   */
  pillPlaceholder: undefined,

  /**
   * Whether or not we have a text pill across all pills,
   * passed along to rendered components, not used
   * @type {Object}
   * @public
   */
  hasTextPill: null,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},


  /**
   * Possible suggestions for pill-value
   */
  valueSuggestions: [],

  /**
   * Is value suggestions API call in progress
   */
  isValueSuggestionsCallInProgress: false,

  didReceiveAttrs() {
    this._super(...arguments);
    if (this.get('cursorPosition') === this.get('newPillPosition')) {
      this.set('isAddNewPill', true);
    }
  },

  actions: {
    triggerNewPill() {
      this.set('isAddNewPill', true);
    },

    handleMessage(type, data) {
      switch (type) {
        case MESSAGE_TYPES.PILL_CREATED:
          this._broadcast(type, data);
          this.set('isAddNewPill', false);
          break;
        case MESSAGE_TYPES.PILL_ADD_CANCELLED:
          this._broadcast(type, data);
          this.set('isAddNewPill', false);
          break;
        case MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW:
          // append is actually an insert to the new pill trigger
          this._broadcast(MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW);
          break;
        case MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT:
          this.get('sendMessage')(MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_LEFT, this.get('newPillPosition'));
          break;
        case MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT:
          this.get('sendMessage')(MESSAGE_TYPES.PILL_TRIGGER_EXIT_FOCUS_TO_RIGHT, this.get('newPillPosition'));
          break;
        case MESSAGE_TYPES.CREATE_FREE_FORM_PILL:
          this.set('isAddNewPill', false);
          this._broadcast(type, data);
          break;
        case MESSAGE_TYPES.CREATE_TEXT_PILL:
          this._broadcast(type, data);
          this.set('isAddNewPill', false);
          break;
        case MESSAGE_TYPES.RECENT_QUERIES_SUGGESTIONS_FOR_TEXT:
          this._broadcast(type, data);
          break;
        case MESSAGE_TYPES.FETCH_VALUE_SUGGESTIONS:
          this._broadcast(type, data);
          break;
        case MESSAGE_TYPES.PILL_PASTE:
          this._broadcast(type, data);
          break;
        case MESSAGE_TYPES.RECENT_QUERY_PILL_CREATED:
          this._broadcast(type, data);
          this.set('isAddNewPill', false);
          break;
        case MESSAGE_TYPES.PILL_OPEN_PAREN:
          this._broadcast(type, data);
          break;
        case MESSAGE_TYPES.PILL_CLOSE_PAREN:
          this._broadcast(type, data);
          break;
        case MESSAGE_TYPES.PILL_LOGICAL_OPERATOR:
          this._broadcast(type, data);
          break;
        case MESSAGE_TYPES.PILL_HOME_PRESSED:
          this._broadcast(type, data);
          break;
        case MESSAGE_TYPES.PILL_END_PRESSED:
          this._broadcast(type, data);
          break;
        case MESSAGE_TYPES.META_DELETE_PRESSED:
          this._broadcast(type, data);
          break;
      }
    }
  },

  _broadcast(type, data) {
    this.get('sendMessage')(type, data, this.get('newPillPosition'));
  }
});