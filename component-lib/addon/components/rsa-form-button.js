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

  withDropdown: match('dropdown',  /standard|split/),

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

  /**
  * Event responsible for calling toggleOptions
  * Skip if not isSplit to allow for action on primary button
  * @public
  */
  click(event) {
    event.stopPropagation();
    if (this.get('withDropdown') && !this.get('isSplit') && !this.get('isDisabled')) {
      this.toggleOptions();
      this.get('eventBus').trigger('rsa-application-click', event.currentTarget);
    }
  },

  didInsertElement() {
    let _this = this;
    this.get('eventBus').on('rsa-application-click', function(targetEl) {
      if (_this.$()) {
        if (!_this.get('optionsCollapsed') && !_this.$().is(targetEl)) {
          _this.collapseOptions();
        }
      }
    });
  },

  actions: {
    /*
    * Template action responsible for calling toggleOptions
    * Skip if and not isDisabled
    * @public
    */
    toggleOptions() {
      if (!this.get('isDisabled')) {
        this.toggleOptions();
      }
    }
  }
});
