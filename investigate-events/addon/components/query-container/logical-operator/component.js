import Component from '@ember/component';
import { alias } from 'ember-computed-decorators';
import * as MESSAGE_TYPES from '../message-types';
import { OPERATOR_AND, OPERATOR_OR } from 'investigate-events/constants/pill';

const AND_LABEL = 'AND';
const OR_LABEL = 'OR';

export default Component.extend({
  classNames: ['pill', 'logical-operator'],
  classNameBindings: ['isFocused', 'isSelected', 'operator'],
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
   * Does the pill have focus?
   * @public
   */
  @alias('pillData.isFocused')
  isFocused: false,

  /**
   * Is the pill selected?
   * @public
   */
  @alias('pillData.isSelected')
  isSelected: false,

  /**
   * The text to display depending upon the type of logical operator
   * @public
   */
  operator: AND_LABEL,

  /**
   * Function to call when sending a message. Default is function stub.
   * @public
   */
  sendMessage: () => {},

  init() {
    this._super(...arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED]: () => this._focusedLeftArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED]: () => this._focusedRightArrowPressed()
    });
  },

  click() {
    const pillData = this.get('pillData');
    const message = pillData.isSelected ? MESSAGE_TYPES.PILL_DESELECTED : MESSAGE_TYPES.PILL_SELECTED;
    this._broadcast(message, pillData);
  },

  didReceiveAttrs() {
    this._super(...arguments);
    const type = this.get('pillData.type');
    if (type === OPERATOR_AND) {
      this.set('operator', AND_LABEL);
    } else if (type === OPERATOR_OR) {
      this.set('operator', OR_LABEL);
    }
  },

  /**
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
   * @param {Object} data Data for message
   * @param {Object} data The event data
   * @private
   */
  _broadcast(type, data) {
    this.get('sendMessage')(type, data, this.get('position'));
  },

  _focusedLeftArrowPressed() {
    this._broadcast(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_LEFT);
  },

  _focusedRightArrowPressed() {
    this._broadcast(MESSAGE_TYPES.PILL_FOCUS_EXIT_TO_RIGHT);
  }
});