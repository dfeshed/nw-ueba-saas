import Component from '@ember/component';

import * as MESSAGE_TYPES from '../message-types';

export default Component.extend({
  classNames: ['new-pill-trigger-container'],

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
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

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
        case MESSAGE_TYPES.PILL_CANCELLED:
          this._broadcast(type, data);
          this.set('isAddNewPill', false);
          break;
        case MESSAGE_TYPES.PILL_ENTERED:
          this._broadcast(MESSAGE_TYPES.PILL_ENTERED);
      }
    }
  },

  _broadcast(type, data) {
    this.get('sendMessage')(type, data, this.get('newPillPosition'));
  }
});