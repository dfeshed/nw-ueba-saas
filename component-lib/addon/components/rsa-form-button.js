import Ember from 'ember';
import layout from '../templates/components/rsa-form-button';

export default Ember.Component.extend({

  layout,

  eventBus: Ember.inject.service('event-bus'),

  tagName: 'div',

  classNames: ['rsa-form-button-wrapper'],

  classNameBindings: ['isStandard',
                      'isPrimary',
                      'isDanger',
                      'isFullWidth',
                      'isCollapsed:is-collapsed:not-collapsed',
                      'withDropdown',
                      'isSplit',
                      'isIconOnly',
                      'isDisabled'],

  isFullWidth: false,

  isCollapsed: true,

  isIconOnly: false,

  isDisabled: false,

  style: 'standard', // ['standard', 'primary', 'danger']

  dropdown: 'none', // ['none', 'standard', 'split']

  isSubmit: Ember.computed.equal('type', 'submit'),

  isStandard: Ember.computed.equal('style', 'standard'),

  isPrimary: Ember.computed.equal('style', 'primary'),

  isDanger: Ember.computed.equal('style', 'danger'),

  isSplit: Ember.computed.equal('dropdown', 'split'),

  withDropdown: Ember.computed.match('dropdown',  /standard|split/),

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
