import Component from '@ember/component';

import * as MESSAGE_TYPES from '../message-types';
import { isBackspace, isDelete, isEnter } from 'investigate-events/util/keys';

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
    if (isBackspace(e) || isDelete(e)) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECTED_FOCUS_DELETE_PRESSED);
    } else if (isEnter(e)) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECTED_FOCUS_ENTER_PRESSED);
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