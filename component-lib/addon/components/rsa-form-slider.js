import Ember from 'ember';
import layout from '../templates/components/rsa-form-slider';

const { Component } = Ember;

export default Component.extend({

  actions: {
    onSliderHandleChange(value) {
      if (this.get('onChange')) {
        this.sendAction('onChange', value);
      }
    },
    onSliderHandleSet(value) {
      if (this.get('onSet')) {
        this.sendAction('onSet', value);
      }
    },
    onSliderHandleUpdate(value) {
      if (this.get('onUpdate')) {
        this.sendAction('onUpdate', value);
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
