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
   * Whether or not we have a text pill across all pills,
   * passed along to rendered components, not used
   * @type {Object}
   * @public
   */
  hasTextPill: null,

  /**
   * List of recent queries
   * @type {Array}
   * @public
   */
  recentQueries: null,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  init() {
    this._super(...arguments);

    // If this trigger should be open as soon as it renders,
    // then set it as such
    if (this.get('startTriggeredPosition') === this.get('newPillPosition')) {
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
          this._broadcast(type, data);
          this.set('isAddNewPill', false);
          break;
        case MESSAGE_TYPES.CREATE_TEXT_PILL:
          this._broadcast(type, data);
          this.set('isAddNewPill', false);
          break;
      }
    }
  },

  _broadcast(type, data) {
    this.get('sendMessage')(type, data, this.get('newPillPosition'));
  }
});