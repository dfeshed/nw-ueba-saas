import Component from '@ember/component';
import { later } from '@ember/runloop';
import computed, { alias, and } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

import * as MESSAGE_TYPES from '../message-types';
import { isEscape, isEnter, isHome, isEnd } from 'investigate-events/util/keys';
import { DELETE_PILL } from 'investigate-events/constants/pill';

export default Component.extend({
  classNames: ['pill', 'complex-pill'],
  classNameBindings: ['isActive', 'isEditing', 'isFocused', 'isInvalid', 'isSelected'],
  tagName: 'div',
  attributeBindings: ['title', 'position'],
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

  text: undefined,

  /**
   * Is this pill currently being edited
   * @type {boolean}
   * @public
   */
  @and('pillData.isEditing', 'isActive')
  isEditing: false,

  /**
   *
   * Does the pill have focus?
   * @public
   */
  @alias('pillData.isFocused')
  isFocused: false,

  /**
   * Update the component once validation completes. A pill is valid if the
   * server side validation passes.
   * @type {boolean}
   * @public
   */
  @alias('pillData.isInvalid')
  isInvalid: false,

  /**
   * Whether or not this pill is selected
   * @type {boolean}
   * @public
   */
  @computed('pillData')
  isSelected: (pillData) => !!pillData && pillData.isSelected,

  /**
   * Update the component title with error message once validation returns
   * If a valid pill, return the filter string
   * @public
   */
  @computed('pillData', 'stringifiedPill', 'isActive')
  title: (pillData, stringifiedPill, isActive) => {
    if (!isActive && pillData) {
      if (pillData.isInvalid) {
        return pillData.validationError ? pillData.validationError.string || pillData.validationError.message : 'Invalid';
      } else {
        return pillData.complexFilterText;
      }
    }
  },

  init() {
    this._super(...arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.DELETE_CLICKED]: (data) => this._deletePill(data),
      [MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED]: (data) => this._deleteOrBackspacePressed(data),
      [MESSAGE_TYPES.FOCUSED_PILL_ENTER_PRESSED]: () => this._focusedEnterPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED]: () => this._focusedLeftArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED]: () => this._focusedRightArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_SHIFT_RIGHT_ARROW_PRESSED]: () => this._focusedShiftRightArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_SHIFT_LEFT_ARROW_PRESSED]: () => this._focusedShiftLeftArrowPressed(),
      [MESSAGE_TYPES.PILL_HOME_PRESSED]: () => this._homeButtonPressed(),
      [MESSAGE_TYPES.PILL_END_PRESSED]: () => this._endButtonPressed()
    });
  },

  didInsertElement() {
    this._super(...arguments);
    if (this.get('isActive')) {
      this.element.querySelector('input').focus();
    }
  },
  didReceiveAttrs() {
    this._super(...arguments);
    if (!this.text) {
      this.text = this.pillData?.complexFilterText;
    }
  },
  click(e) {

    // If event was triggered through delete-pill, no need
    // to propogate actions related to selection/focus as
    // the pill will soon be deleted and removed from dom
    if (e?.target?.classList?.contains(DELETE_PILL)) {
      return;
    }
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

  _pillOpenForEdit() {
    const pillData = this.get('pillData');
    if (!this.text) {
      this.text = pillData.complexFilterText;
    }
    this._broadcast(MESSAGE_TYPES.PILL_OPEN_FOR_EDIT, pillData);
  },

  _pillSelected() {
    // Waiting 300 milliseconds in order to give
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
    }, 300);
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
    },

    onKeyDown(event) {
      if (isEnter(event) && event.target.value.trim().length > 0) {
        this._editPill(event.target.value);
      } else if (isEscape(event)) {
        this._cancelPillEdit();
      } else if (isHome(event) && event.target.value.trim().length === 0) {
        this._homeButtonPressed();
      } else if (isEnd(event) && event.target.value.trim().length === 0) {
        this._endButtonPressed();
      }
    },

    handleInput(event) {
      if (!this.isDestroyed && !this.isDestroying) {
        this.set('text', event.target.value);
      }
    }
  },

  _editPill(data) {
    const pillData = this.get('pillData');
    const newPillData = {
      ...pillData,
      complexFilterText: data
    };
    this._broadcast(MESSAGE_TYPES.PILL_EDITED, newPillData);
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
  _deleteOrBackspacePressed(data) {
    this._broadcast(MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, {
      ...data,
      pillData: this.get('pillData')
    });
  },

  _focusedEnterPressed() {
    if (!this.get('isActive')) {
      const pillData = this.get('pillData');
      this._broadcast(MESSAGE_TYPES.ENTER_PRESSED_ON_FOCUSED_PILL, pillData);
    }
  },

  _focusedLeftArrowPressed() {
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT);
    }
  },

  _focusedRightArrowPressed() {
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT);
    }
  },

  _focusedShiftRightArrowPressed() {
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.SELECT_ALL_PILLS_TO_RIGHT);
    }
  },

  _focusedShiftLeftArrowPressed() {
    if (!this.get('isActive')) {
      this._broadcast(MESSAGE_TYPES.SELECT_ALL_PILLS_TO_LEFT);
    }
  },

  /**
   * Handles events propagating from focus-holder
   * when the user clicks on Home button and relays the message.
   * When editing the pill, the event is not relayed unless the
   * pill data is empty.
   */
  _homeButtonPressed() {
    this._broadcast(MESSAGE_TYPES.PILL_HOME_PRESSED, this.get('pillData'));
  },

  /**
   * Handles events propagating from focus-holder
   * when the user clicks on End button and relays the message.
   * When editing the pill, the event is not relayed unless the
   * pill data is empty.
   */
  _endButtonPressed() {
    this._broadcast(MESSAGE_TYPES.PILL_END_PRESSED, this.get('pillData'));
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
