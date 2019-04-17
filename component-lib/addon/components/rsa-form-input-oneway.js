import Component from '@ember/component';
import { or } from 'ember-computed';
import layout from '../templates/components/rsa-form-input-oneway';

export default Component.extend({
  layout,

  tagName: 'label',

  classNames: ['rsa-form-input'],

  attributeBindings: ['testId:test-id'],
  classNameBindings: [
    'isDisabled',
    'isError',
    'isInline',
    'isReadOnly',
    'isSuccess'
  ],

  errorMessage: null,
  isDisabled: false,
  isError: false,
  isInline: false,
  isReadOnly: false,
  isSuccess: false,

  // input label text
  label: null,

  // <input> attributes
  type: 'text',
  value: null,
  autocomplete: 'on', // 'on' | 'off'
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

  // reference to the internal <input>
  _inputEl: null,

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
    this._inputEl = this.element.querySelector('input');
    this._inputEl.addEventListener('keyup', this.handleKeyUp);
  },

  willDestroyElement() {
    this._super(...arguments);
    this._inputEl.removeEventListener('keyup', this.handleKeyUp);
  },

  actions: {
  }

});
