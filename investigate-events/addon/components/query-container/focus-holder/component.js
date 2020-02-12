import Component from '@ember/component';
import { throttle } from '@ember/runloop';

import * as MESSAGE_TYPES from '../message-types';
import KEY_MAP, { isDelete, isBackspace } from 'investigate-events/util/keys';

export default Component.extend({
  classNames: ['focus-holder'],
  tagName: 'span',

  /**
   * An action to call when sending messages and data to the parent component.
   * @type {function}
   * @public
   */
  sendMessage: () => {},

  /**
   * This function is called every time a key is pressed.
   * @private
   */
  keyDownHandlerMap: undefined,

  /**
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
   * @param {Object} data The event data
   * @private
   */
  _broadcast(type, data) {
    this.get('sendMessage')(type, data);
  },

  init() {
    this._super(...arguments);
    this.set('keyDownHandlerMap', {
      [KEY_MAP.delete.key]: this._backspaceDeleteHandler.bind(this),
      [KEY_MAP.backspace.key]: this._backspaceDeleteHandler.bind(this),
      [KEY_MAP.enter.key]: this._commandHandler.bind(this),
      [KEY_MAP.arrowRight.key]: this._rightArrowHandler.bind(this),
      [KEY_MAP.arrowLeft.key]: this._leftArrowHandler.bind(this),
      [KEY_MAP.home.key]: this._homeHandler.bind(this),
      [KEY_MAP.end.key]: this._endHandler.bind(this),
      [KEY_MAP.openParen.key]: this._parenHandler.bind(this),
      [KEY_MAP.Key_a.key]: this._ctrlAHandler.bind(this),
      [KEY_MAP.Key_A.key]: this._ctrlAHandler.bind(this)
    });
  },

  didRender() {
    const { element } = this;
    element.querySelector('input').focus();
  },

  _ctrlAHandler({ e }) {
    if (e.ctrlKey) {
      this._broadcast(MESSAGE_TYPES.FOCUSED_PILL_CTRL_A_PRESSED);
    }
  },

  _backspaceDeleteHandler({ e }) {
    this._broadcast(MESSAGE_TYPES.PILL_DELETE_OR_BACKSPACE_PRESSED, {
      isFocusedPill: true,
      isDeleteEvent: isDelete(e),
      isBackspaceEvent: isBackspace(e)
    });
    // Firefox by default redirects to the previous page.
    // This is to prevent from going back.
    e.preventDefault();
  },

  _commandHandler() {
    this._broadcast(MESSAGE_TYPES.FOCUSED_PILL_ENTER_PRESSED);
  },

  _rightArrowHandler({ e }) {
    if (e.shiftKey) {
      this._broadcast(MESSAGE_TYPES.FOCUSED_PILL_SHIFT_RIGHT_ARROW_PRESSED);
    } else {
      this._broadcast(MESSAGE_TYPES.FOCUSED_PILL_RIGHT_ARROW_PRESSED);
    }
  },

  _leftArrowHandler({ e }) {
    if (e.shiftKey) {
      this._broadcast(MESSAGE_TYPES.FOCUSED_PILL_SHIFT_LEFT_ARROW_PRESSED);
    } else {
      this._broadcast(MESSAGE_TYPES.FOCUSED_PILL_LEFT_ARROW_PRESSED);
    }
  },

  _homeHandler() {
    this._broadcast(MESSAGE_TYPES.PILL_HOME_PRESSED);
  },

  _endHandler() {
    this._broadcast(MESSAGE_TYPES.PILL_END_PRESSED);
  },

  _parenHandler() {
    this._broadcast(MESSAGE_TYPES.FOCUSED_PILL_OPEN_PAREN_PRESSED);
  },

  keyDown(e) {
    const handler = this.keyDownHandlerMap[e.key];
    return handler ? throttle(this, handler, { e }, 1000) : false;
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