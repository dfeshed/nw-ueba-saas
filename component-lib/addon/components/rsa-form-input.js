import Component from '@ember/component';
import { or } from 'ember-computed';
import layout from '../templates/components/rsa-form-input';

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
  maxLength: null,
  errorMessage: null,
  isDisabled: false,
  isError: false,
  isInline: false,
  isReadOnly: false,
  isSuccess: false,
  label: null,
  placeholder: null,
  type: 'text',
  value: null,

  resolvedDisabled: or('isDisabled', 'isReadOnly')
});
