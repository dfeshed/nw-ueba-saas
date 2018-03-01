import Component from '@ember/component';
import { equal } from 'ember-computed';
import { inject as service } from '@ember/service';
import layout from '../templates/components/rsa-form-button';

export default Component.extend({
  layout,

  eventBus: service(),

  tagName: 'div',

  classNames: ['rsa-form-button-wrapper'],

  attributeBindings: ['title', 'testId:test-id'],

  classNameBindings: [
    'isActive',
    'isDanger',
    'isDisabled',
    'isFullWidth',
    'isIconOnly',
    'isPrimary',
    'isStandard',
    'withDropdown'
  ],

  style: 'standard', // ['standard', 'primary', 'danger']

  allowToggleActive: false,
  defaultAction: null,
  isActive: false,
  isFullWidth: false,
  isIconOnly: false,
  isDisabled: false,
  withDropdown: false,

  isDanger: equal('style', 'danger'),
  isPrimary: equal('style', 'primary'),
  isStandard: equal('style', 'standard'),
  isSubmit: equal('type', 'submit'),

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
