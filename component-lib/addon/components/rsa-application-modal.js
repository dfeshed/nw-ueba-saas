import Ember from 'ember';
import layout from '../templates/components/rsa-application-modal';

export default Ember.Component.extend({

  layout,

  eventBus: Ember.inject.service('event-bus'),

  classNames: ['rsa-application-modal'],

  classNameBindings: ['isOpen'],

  eventId: null,

  isOpen: false,

  init() {
    this.listen();
    this._super(arguments);
  },

  didInsertElement() {
    let that = this;

    Ember.run.schedule('afterRender', function() {
      that.$('.modal-close').on('click', function() {
        Ember.run.next(that, function() {
          that.closeModal();
        });
      });
    });
  },

  listen() {
    this.get('eventBus').on(`rsa-application-modal-open-${this.get('eventId')}`, this, 'openModal');
    this.get('eventBus').on(`rsa-application-modal-close-${this.get('eventId')}`, this, 'closeModal');
    this.get('eventBus').on('rsa-application-modal-close-all', this, 'closeModal');
  },

  updateModal(truth) {
    this.get('eventBus').trigger('rsa-application-modal-did-open', truth);
    this.set('isOpen', truth);

    if (truth) {
      Ember.$('#modalDestination').addClass('active');
    } else {
      Ember.$('#modalDestination').removeClass('active');
    }
  },

  openModal() {
    let isOpen = true;
    Ember.run.next(this, function() {
      this.updateModal(isOpen);
    });
  },

  closeModal() {
    let isOpen = false;
    Ember.run.next(this, function() {
      this.updateModal(isOpen);
    });
  },

  keyUp(e) {
    if (e.keyCode === 27) {
      this.closeModal();
    }
  },

  click() {
    if (!this.get('isOpen')) {
      this.openModal();
    }
  },

  actions: {
    openModal() {
      this.openModal();
    },

    closeModal() {
      this.closeModal();
    }
  }

});
