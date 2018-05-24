import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';

export default Component.extend({
  tagName: '',

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  actions: {
    /**
     * Sends message to parent to delete the pill this is attached to
     * @private
     */
    deletePill() {
      this.get('sendMessage')(MESSAGE_TYPES.PILL_DELETED);
    }
  }
});