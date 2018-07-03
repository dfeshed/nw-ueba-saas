import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';

export default Component.extend({
  tagName: 'span',

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  // Send PILL DELETED action up, but trap the click
  // event
  click() {
    this.get('sendMessage')(MESSAGE_TYPES.PILL_DELETED);
    return false;
  }
});