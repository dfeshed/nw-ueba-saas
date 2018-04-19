import Component from '@ember/component';
import * as MESSAGE_TYPES from '../message-types';

// const { log } = console;

export default Component.extend({
  classNameBindings: ['isActive', ':pill-value'],

  isActive: false,
  initialKeyUp: true,
  sendMessage: () => {},
  valueString: null,

  didRender() {
    const input = this.element.querySelector('input');
    if (input) {
      input.focus();
    }
  },

  actions: {
    onKeyUp(input, event) {
      // For some reason, when this component is activated and creates the
      //  input, it's reacting to the Enter key pressed from the pill-operator
      // component and sends out the VALUE_ENTER_KEY event. It should not do
      // this.
      if (this.get('initialKeyUp') && event.keyCode === 13) {
        this.toggleProperty('initialKeyUp');
        return;
      }
      // 'keyCode' is deprecated in favor of 'key', but the Ember test-helpers
      // don't support 'key'
      switch (event.keyCode) {
        case 13:// Enter
          this._broadcast(MESSAGE_TYPES.VALUE_ENTER_KEY);
          break;
        case 27:// Escape
          this._broadcast(MESSAGE_TYPES.VALUE_ESCAPE_KEY);
          break;
        case 8:// Backspace
          this._broadcast(MESSAGE_TYPES.VALUE_BACKSPACE_KEY, input);
          break;
        case 37:// ArrowLeft
          this._broadcast(MESSAGE_TYPES.VALUE_ARROW_LEFT_KEY, input);
          break;
        case 39:// ArrowRight
          this._broadcast(MESSAGE_TYPES.VALUE_ARROW_RIGHT_KEY, input);
          break;
        default:
          this._broadcast(MESSAGE_TYPES.VALUE_SET, input);
      }
    }
  },

  // ************************************************************************ //
  //                          PRIVATE FUNCTIONS                               //
  // ************************************************************************ //
  /**
   * Sends messages to the parent container.
   * @param {string} type The event type from `event-types`
   * @param {Object} data The event data
   * @private
   */
  _broadcast(type, data) {
    this.get('sendMessage')(type, data);
  }
});
