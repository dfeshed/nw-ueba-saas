import Component from '@ember/component';
import { throttle } from '@ember/runloop';

import * as MESSAGE_TYPES from '../message-types';
import { isBackspace, isDelete, isEnter, isArrowRight, isArrowLeft, isShift, isHome, isEnd } from 'investigate-events/util/keys';

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
    const { element } = this;
    element.querySelector('input').focus();
  },

  throttleKeyDown({ e }) {
    const evtobj = window.event ? event : e;
    if (isBackspace(evtobj) || isDelete(evtobj)) {
      this.get('sendMessage')(MESSAGE_TYPES.FOCUSED_PILL_DELETE_PRESSED);
      // Firefox by default redirects to the previous page.
      // This is to prevent from going back.
      e.preventDefault();
    } else if (isEnter(evtobj)) {
      this.get('sendMessage')(MESSAGE_TYPES.FOCUSED_PILL_ENTER_PRESSED);
    } else if (isArrowRight(evtobj) && evtobj.shiftKey) {
      this.get('sendMessage')(MESSAGE_TYPES.FOCUSED_PILL_SHIFT_RIGHT_ARROW_PRESSED);
    } else if (isArrowLeft(evtobj) && evtobj.shiftKey) {
      this.get('sendMessage')(MESSAGE_TYPES.FOCUSED_PILL_SHIFT_LEFT_ARROW_PRESSED);
    } else if (isArrowLeft(evtobj)) {
      this.get('sendMessage')(MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED);
    } else if (isArrowRight(evtobj)) {
      this.get('sendMessage')(MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED);
    } else if (isHome(evtobj)) {
      this.get('sendMessage')(MESSAGE_TYPES.PILL_HOME_PRESSED);
    } else if (isEnd(evtobj)) {
      this.get('sendMessage')(MESSAGE_TYPES.PILL_END_PRESSED);
    }
    return false;
  },

  keyDown(e) {
    if (isShift(e)) {
      return false;
    } else {
      throttle(this, this.throttleKeyDown, { e }, 1000);
    }
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