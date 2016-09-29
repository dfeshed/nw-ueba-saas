import Ember from 'ember';
import layout from '../templates/components/rsa-form-button';

const {
  Component,
  inject: {
    service
  },
  computed: {
    equal,
    match
  }
} = Ember;

export default Component.extend({

  layout,

  eventBus: service(),

  tagName: 'div',

  classNames: ['rsa-form-button-wrapper'],

  classNameBindings: [
    'isCollapsed:is-collapsed:not-collapsed',
    'isDanger',
    'isDisabled',
    'isFullWidth',
    'isPrimary',
    'isStandard',
    'positionOptions',
    'isSplit',
    'isIconOnly',
    'withDropdown'
  ],

  isFullWidth: false,

  isCollapsed: true,

  isIconOnly: false,

  isDisabled: false,

  positionOptions: 'left',

  style: 'standard', // ['standard', 'primary', 'danger']

  dropdown: 'none', // ['none', 'standard', 'split']

  isSubmit: equal('type', 'submit'),

  isStandard: equal('style', 'standard'),

  isPrimary: equal('style', 'primary'),

  isDanger: equal('style', 'danger'),

  isSplit: equal('dropdown', 'split'),

  withDropdown: match('dropdown', /standard|split/),

  defaultAction: null,

  /**
  * Responsible for toggling visibility of dropdown list
  * @public
  */
  toggleOptions() {
    this.toggleProperty('isCollapsed');
  },

  collapseOptions() {
    this.set('isCollapsed', true);
  },

  didInsertElement() {
    this.get('eventBus').on('rsa-application-click', (targetEl) => {
      if (this.get('isSplit')) {
        if (!this.$('.expand i').is(targetEl)) {
          this.collapseOptions();
        }
      } else {
        if (this.$('.rsa-form-button') && !this.$('.rsa-form-button').is(targetEl) && !this.$('.expand').is(targetEl) && !this.$('.expand i').is(targetEl)) {
          this.collapseOptions();
        }
      }
    });
  },

  actions: {

    defaultAction() {
      if (!this.get('isDisabled')) {
        this.sendAction('defaultAction');
      }
    },
    /*
    * Template action responsible for calling toggleOptions
    * Skip if isDisabled
    * @public
    */
    toggleOptions() {
      if (!this.get('isDisabled')) {
        this.toggleOptions();
      }
    }
  }
});
