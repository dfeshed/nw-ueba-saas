import Component from '@ember/component';
import { or } from 'ember-computed';
import layout from '../templates/components/rsa-form-textarea';

export default Component.extend({
  layout,

  tagName: 'label',

  classNames: ['rsa-form-textarea'],

  classNameBindings: [
    'isDisabled',
    'isError',
    'isReadOnly',
    'isSuccess'],

  isDisabled: false,
  isError: false,
  isReadOnly: false,
  isSuccess: false,
  label: null,
  placeholder: null,
  value: null,

  resolvedDisabled: or('isDisabled', 'isReadOnly')
});
