import Component from '@ember/component';

import * as MESSAGE_TYPES from '../message-types';
import KEY_MAP from 'investigate-events/util/keys';

export default Component.extend({
  classNames: ['focus-holder'],
  tagName: 'span',

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  didRender() {
    this.$('input').focus();
  },

  keyDown(e) {
    if (e.keyCode === KEY_MAP.delete.code || e.keyCode === KEY_MAP.backspace.code) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECTED_FOCUS_DELETE_PRESSED);
    }
    return false;
  },

  // No need to bubble up these events
  keyUp() {
    return false;
  },

  // No need to bubble up these events
  keyPress() {
    return false;
  }
});