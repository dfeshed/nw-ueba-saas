import Ember from 'ember';
import layout from '../templates/components/rsa-form-input';

const {
  Component,
  computed: {
    or
  }
} = Ember;

export default Component.extend({
  layout,

  tagName: 'label',

  classNames: ['rsa-form-input'],

  classNameBindings: ['isReadOnly',
                      'isDisabled',
                      'isError',
                      'isSuccess',
                      'isInline'],

  value: null,

  label: null,

  placeholder: null,

  type: 'text',

  isInline: false,

  isReadOnly: false,

  isDisabled: false,

  isError: false,

  errorMessage: null,

  isSuccess: false,

  resolvedDisabled: or('isDisabled', 'isReadOnly')

});