import Ember from 'ember';
import layout from '../templates/components/rsa-form-textarea';

export default Ember.Component.extend({

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

  resolvedDisabled: Ember.computed.or('isDisabled', 'isReadOnly')

});
