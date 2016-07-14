import Ember from 'ember';
import layout from '../templates/components/rsa-form-textarea';

const {
  Component,
  computed: {
    or
  }
} = Ember;

export default Component.extend({

  layout,

  tagName: 'label',

  classNames: ['rsa-form-textarea'],

  classNameBindings: ['isReadOnly',
                      'isDisabled',
                      'isError',
                      'isSuccess'],

  value: null,

  label: null,

  placeholder: null,

  isReadOnly: false,

  isDisabled: false,

  isError: false,

  isSuccess: false,

  resolvedDisabled: or('isDisabled', 'isReadOnly')

});
