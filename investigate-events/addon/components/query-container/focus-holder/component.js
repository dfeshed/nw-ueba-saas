import Component from '@ember/component';
import { debounce } from '@ember/runloop';

import * as MESSAGE_TYPES from '../message-types';
import { isBackspace, isDelete, isEnter, isArrowDown, isArrowUp, isArrowRight, isArrowLeft } from 'investigate-events/util/keys';

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

  debounceKeyDown({ e }) {
    const evtobj = window.event ? event : e;
    if (isBackspace(evtobj) || isDelete(evtobj)) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECTED_FOCUS_DELETE_PRESSED);
    } else if (isEnter(evtobj)) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECTED_FOCUS_ENTER_PRESSED);
    } else if ((isArrowDown(evtobj) || isArrowRight(evtobj)) && evtobj.shiftKey) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECTED_FOCUS_SHIFT_DOWN_RIGHT_ARROW_PRESSED);
    } else if ((isArrowUp(evtobj) || isArrowLeft(evtobj)) && evtobj.shiftKey) {
      this.get('sendMessage')(MESSAGE_TYPES.SELECTED_FOCUS_SHIFT_UP_LEFT_ARROW_PRESSED);
    }
    return false;
  },

  keyDown(e) {
    debounce(this, this.debounceKeyDown, { e }, 1000);
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