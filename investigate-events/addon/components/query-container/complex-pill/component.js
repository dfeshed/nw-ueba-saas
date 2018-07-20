import Component from '@ember/component';
import computed from 'ember-computed-decorators';

import * as MESSAGE_TYPES from '../message-types';

export default Component.extend({
  classNames: ['complex-pill'],
  tagName: 'span',

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

  /**
   * Is this pill able to be deleted?
   * @type {boolean}
   * @public
   */
  @computed('isActive')
  isDeletable: (isActive) => !isActive,

  init() {
    this._super(arguments);
    this.set('_messageHandlerMap', {
      [MESSAGE_TYPES.DELETE_CLICKED]: (data) => this._deletePill(data)
    });
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
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
   * @param {Object} data The event data
   * @private
   */
  _broadcast(type, data) {
    this.get('sendMessage')(type, data, this.get('position'));
  }
});