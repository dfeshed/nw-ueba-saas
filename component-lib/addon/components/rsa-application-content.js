import Ember from 'ember';
import layout from '../templates/components/rsa-application-content';

const {
  Component,
  inject: {
    service
  },
  run
} = Ember;

export default Component.extend({

  layout,

  tagName: 'page',

  eventBus: service('event-bus'),

  classNames: ['rsa-application-content'],

  classNameBindings: ['hasBlur'],

  hasBlur: false,

  toggleBlur(truth) {
    run.next(this, function() {
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
