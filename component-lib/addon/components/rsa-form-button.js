import Ember from 'ember';
import layout from '../templates/components/rsa-form-button';

const {
  Component,
  inject: {
    service
  },
  computed: {
    equal
  }
} = Ember;

export default Component.extend({

  layout,

  eventBus: service(),

  tagName: 'div',

  classNames: ['rsa-form-button-wrapper'],

  attributeBindings: ['title'],

  classNameBindings: [
    'isDanger',
    'isDisabled',
    'isFullWidth',
    'isPrimary',
    'isStandard',
    'isIconOnly',
    'withDropdown',
    'isActive'
  ],

  style: 'standard', // ['standard', 'primary', 'danger']

  isFullWidth: false,

  isIconOnly: false,

  isDisabled: false,

  withDropdown: false,

  defaultAction: null,

  isActive: false,

  allowToggleActive: false,

  isSubmit: equal('type', 'submit'),

  isStandard: equal('style', 'standard'),

  isPrimary: equal('style', 'primary'),

  isDanger: equal('style', 'danger'),

  click() {
    if (this.get('allowToggleActive') && !this.get('isDisabled')) {
      this.toggleProperty('isActive');
    }
  },

  actions: {

    defaultAction() {
      if (!this.get('isDisabled')) {
        this.sendAction('defaultAction');
      }
    }

  }
});
