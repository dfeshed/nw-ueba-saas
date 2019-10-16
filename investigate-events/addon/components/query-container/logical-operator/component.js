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

  didReceiveAttrs() {
    this._super(...arguments);
    const type = this.get('pillData.type');
    if (type === OPERATOR_AND) {
      this.set('operator', AND_LABEL);
    } else if (type === OPERATOR_OR) {
      this.set('operator', OR_LABEL);
    }
  },

  init() {
    this._super(...arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED]: () => this._focusedLeftArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED]: () => this._focusedRightArrowPressed(),
      [MESSAGE_TYPES.FOCUSED_PILL_ENTER_PRESSED]: () => this._operatorToggled(),
      [MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED]: (data) => this._deleteOrBackspacePressed(data)
    });
  },

  _operatorToggled() {
    this.get('sendMessage')(
      MESSAGE_TYPES.PILL_LOGICAL_OPERATOR_TOGGLED,
      this.get('pillData'),
      this.get('position')
    );
  },

  click() {
    this._operatorToggled();
  },

  /**
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
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
  },

  _deleteOrBackspacePressed(data) {
    this._broadcast(MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, data);
  },

  actions: {
    /**
     * Handler for all messages coming from sub components.
     * @param {string} type The event type from `message-types`
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
        this._broadcast(type);
      }
    }
  }

});