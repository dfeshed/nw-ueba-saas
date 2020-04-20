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

  // Send PILL DELETED action up
  // Propagating this click event up to parent component so that clicks to
  // delete icon are recognized elsewhere in application. ASOC-80113 #3 for more info.
  click() {
    this.get('sendMessage')(MESSAGE_TYPES.DELETE_CLICKED);
  }
});