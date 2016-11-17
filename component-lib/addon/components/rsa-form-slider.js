import Ember from 'ember';
import layout from '../templates/components/rsa-form-slider';

const { Component } = Ember;

export default Component.extend({

  actions: {
    onSliderHandleChange(value) {
      if (this.get('onChange')) {
        this.sendAction('onChange', value);
      }
    }
  },
  layout,

  classNames: ['rsa-form-slider'],

  classNameBindings: [
    'isError',
    'isDisabled',
    'isReadOnly'],

  start: null,

  step: null,

  range: null,

  connect: true,

  tooltips: true,

  model: null,

  isError: false,

  isDisabled: false,

  isReadOnly: false

});
