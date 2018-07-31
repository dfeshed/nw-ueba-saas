import Component from '@ember/component';
import { later } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

import * as MESSAGE_TYPES from '../message-types';
import { isEscape } from 'investigate-events/util/keys';

export default Component.extend({
  classNames: ['complex-pill'],
  classNameBindings: ['isActive', 'isSelected'],
  tagName: 'span',
  attributeBindings: ['title'],
  i18n: service(),

  pillData: undefined,

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  // Not used now, but will be set to true
  // when editing later
  isActive: false,

  // Whether or not a double click has fired
  doubleClickFired: false,

  /**
   * Whether or not this pill is selected
   * @type {boolean}
   * @public
   */
  @computed('pillData')
  isSelected: (pillData) => !!pillData && pillData.isSelected,

  @computed('pillData')
  title(pillData) {
    const notEditable = this.get('i18n').t('queryBuilder.notEditable').string;
    return `${notEditable}\n${pillData.complexFilterText}`;
  },

  init() {
    this._super(arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.DELETE_CLICKED]: (data) => this._deletePill(data),
      [MESSAGE_TYPES.SELECTED_FOCUS_DELETE_PRESSED]: () => this._selectedFocusDeletePressed(),
      [MESSAGE_TYPES.SELECTED_FOCUS_ENTER_PRESSED]: () => this._selectedFocusEnterPressed(),
      [MESSAGE_TYPES.SELECTED_FOCUS_SHIFT_DOWN_RIGHT_ARROW_PRESSED]: () => this._selectedFocusShiftDownRightArrowPressed(),
      [MESSAGE_TYPES.SELECTED_FOCUS_SHIFT_UP_LEFT_ARROW_PRESSED]: () => this._selectedFocusShiftUpLeftArrowPressed()
    });
  },

  click() {
    // If not active, and clicking pill, then process
    // this as a selection event
    if (!this.get('isActive')) {
      this._pillSelected();
    }
  },

  doubleClick() {
    // set flag to prevent previous single-clicks from executing
    this.set('doubleClickFired', true);

    // If not active, and double clicking pill, then process
    // this as a desire to open for edit
    if (!this.get('isActive')) {
      this._pillOpenForEdit();
    }
  },

  keyDown(event) {
    // if the key pressed is an escape, then bubble that out and
    // escape further processing
    if (isEscape(event)) {
      // Let others know ECS was pressed
      this._cancelPillEdit();
    }
  },

  _pillOpenForEdit() {
    const pillData = this.get('pillData');
    this._broadcast(MESSAGE_TYPES.PILL_OPEN_FOR_EDIT, pillData);
  },

  _pillSelected() {
    // Waiting 175 milliseconds in order to give
    // a double click a chance to occur. If we do not
    // delay execution here, double clicks will execute
    // click processing twice.
    later(() => {
      if (!this.get('doubleClickFired')) {
        const pillData = this.get('pillData');
        if (pillData.isSelected) {
          this._broadcast(MESSAGE_TYPES.PILL_DESELECTED, pillData);
        } else {
          this._broadcast(MESSAGE_TYPES.PILL_SELECTED, pillData);
        }
      } else {
        // if double click had been fired, reset flag, however
        // decent chance component no longer exists, so make
        // that check
        if (!this.isDestroyed || !this.isDestroying) {
          this.set('doubleClickFired', false);
        }
      }
    }, 175);
  },

  _cancelPillEdit() {
    const pD = this.get('pillData');
    // Inform container that this pill component is cancelling out of editing
    this._broadcast(MESSAGE_TYPES.PILL_EDIT_CANCELLED, pD);
  },

  actions: {
    /**
     * Handler for all messages coming from pill components
     * (meta/operator/value).
     * @param {string} type The event type from `event-types`
     * @param {Object} data The event data
     * @public
     */
    handleMessage(type, data) {
      const messageHandlerFn = this.get('_messageHandlerMap')[type];
      if (messageHandlerFn) {
        messageHandlerFn(data);
      } else {
        // Any messages that do not match expected message types get send up
        // to the query-pills component.
        this._broadcast(type, data);
      }
    }
  },

  /**
   * Handles messaging around deleting this pill
   * @private
   */
  _deletePill() {
    this._broadcast(MESSAGE_TYPES.PILL_DELETED, this.get('pillData'));
  },

  /**
   * Handles events propagating from focus-holder
   * This will be called only when a pill is selected and
   * user presses either delete or backspace
   * @private
   */
  _selectedFocusDeletePressed() {
    this.get('sendMessage')(MESSAGE_TYPES.DELETE_PRESSED_ON_SELECTED_PILL);
  },

  _selectedFocusEnterPressed() {
    if (!this.get('isActive')) {
      const pillData = this.get('pillData');
      this._broadcast(MESSAGE_TYPES.ENTER_PRESSED_ON_SELECTED_PILL, pillData);
    }
  },

  _selectedFocusShiftDownRightArrowPressed() {
    if (!this.get('isActive')) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT, this.get('position'));
    }
  },

  _selectedFocusShiftUpLeftArrowPressed() {
    if (!this.get('isActive')) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT, this.get('position'));
    }
  },

    /**
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
   * @param {Object} data The event data
   * @private
   */
  _broadcast(type, data) {
    this.get('sendMessage')(type, data, this.get('position'));
  }
});