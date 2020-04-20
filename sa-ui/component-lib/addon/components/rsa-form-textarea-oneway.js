import Component from '@ember/component';
import { or } from 'ember-computed';
import layout from '../templates/components/rsa-form-textarea-oneway';

export default Component.extend({
  layout,

  tagName: 'label',

  classNames: ['rsa-form-textarea'],

  classNameBindings: [
    'isDisabled',
    'isError',
    'isReadOnly',
    'isSuccess'],

  errorMessage: null,
  isDisabled: false,
  isError: false,
  isReadOnly: false,
  isSuccess: false,

  // textarea label text
  label: null,

  // <textarea> attributes
  value: null,
  // autocomplete: 'on', // 'on' | 'off' // currently only firefox and partially safari
  autofocus: false,
  resolvedDisabled: or('isDisabled', 'isReadOnly'),
  maxLength: null,
  placeholder: null,
  spellcheck: true,

  // actions expected to be passed in
  onEnter: function onEnter() {},
  onFocusOut: function onFocusOut() {},
  onKeyUp: function onKeyUp() {},
  onKeyDown: function onKeyDown() {},

  // reference to the internal <textarea>
  _textareaEl: null,

  // private listener to use with onEnter
  _handleKeyUp(event) {
    if (event.keyCode === 13) {
      this.get('onEnter')(event);
    }
  },
  // reference to the bound version of _handleKeyUp
  handleKeyUp: null,

  init() {
    this._super(...arguments);
    // bind for proper 'this' context, and for 'add/removeEventListener()' to work properly
    this.handleKeyUp = this._handleKeyUp.bind(this);
  },

  didInsertElement() {
    this._super(...arguments);
    this._textareaEl = this.element.querySelector('textarea');
    this._textareaEl.addEventListener('keyup', this.handleKeyUp);
  },

  willDestroyElement() {
    this._super(...arguments);
    this._textareaEl.removeEventListener('keyup', this.handleKeyUp);
  },

  actions: {
  }

});
