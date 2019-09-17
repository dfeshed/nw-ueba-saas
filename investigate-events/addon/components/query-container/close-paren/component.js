import Component from '@ember/component';
import { alias } from 'ember-computed-decorators';

import * as MESSAGE_TYPES from '../message-types';

export default Component.extend({
  classNames: ['close-paren'],
  classNameBindings: ['isFocused', 'isSelected', 'isTwinFocused'],
  attributeBindings: ['position'],

  /**
   * The position of this pill relative to other pills.
   * Used when messaging up to parent.
   * @type {Number}
   * @public
   */
  position: null,

  /**
   * Pre-populated Pill Data.
   * @type {Object}
   * @public
   */
  pillData: null,

  /**
   *
   * Does the pill have focus?
   * @public
   */
  @alias('pillData.isFocused')
  isFocused: false,

  /**
   *
   * Is the pill selected?
   * @public
   */
  @alias('pillData.isSelected')
  isSelected: false,

  /**
   *
   * Is the the open paren that is the twin to this close
   * focused so that this needs to show style for that?
   * @public
   */
  @alias('pillData.isTwinFocused')
  isTwinFocused: false,

  init() {
    this._super(...arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.FOCUSED_PILL_DELETE_PRESSED]: () => this._focusedDeletePressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED]: () => this._focusedLeftArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED]: () => this._focusedRightArrowPressed()
    });
  },

  click() {
    const pillData = this.get('pillData');
    if (pillData.isSelected) {
      this.get('sendMessage')(MESSAGE_TYPES.PILL_DESELECTED, pillData, this.get('position'));
    } else {
      this.get('sendMessage')(MESSAGE_TYPES.PILL_SELECTED, pillData, this.get('position'));
    }
  },

  /**
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
   * @param {Object} data The event data
   * @private
   */
  _broadcast(type) {
    this.get('sendMessage')(type, this.get('position'));
  },

  _focusedLeftArrowPressed() {
    this._broadcast(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT);
  },

  _focusedRightArrowPressed() {
    this._broadcast(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT);
  },

  _focusedDeletePressed() {
    this.get('sendMessage')(MESSAGE_TYPES.DELETE_PRESSED_ON_FOCUSED_PILL, this.get('pillData'));
  },

  actions: {
    /**
     * Handler for all messages coming from sub components.
     * @param {string} type The event type from `message-types`
     * @param {Object} data The event data
     * @public
     */
    handleMessage(type) {
      const messageHandlerFn = this.get('_messageHandlerMap')[type];
      if (messageHandlerFn) {
        messageHandlerFn();
      } else {
        // Any messages that do not match expected message types get send up
        // to the query-pills component.
        this._broadcast(type, this.get('position'));
      }
    }

  }
});