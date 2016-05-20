import Ember from 'ember';
import layout from '../templates/components/rsa-application-content';

export default Ember.Component.extend({

  layout,

  eventBus: Ember.inject.service('event-bus'),

  classNames: ['rsa-application-content'],

  classNameBindings: ['hasBlur'],

  hasBlur: false,

  toggleBlur(truth) {
    Ember.run.next(this, function() {
      this.set('hasBlur', truth);
    });
  },

  listen() {
    this.get('eventBus').on('rsa-application-modal-did-open', this, 'toggleBlur');
  },

  click(event) {
    this.get('eventBus').trigger('rsa-application-click', event.target);
  },

  init() {
    this.listen();
    this._super(arguments);
  }

});