import Ember from 'ember';
import layout from '../templates/components/rsa-application-modal';

const {
  Component,
  inject: {
    service
  },
  run,
  $
} = Ember;

export default Component.extend({

  layout,

  eventBus: service(),

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

    run.schedule('afterRender', function() {
      that.$('.modal-close').on('click', function() {
        run.next(that, function() {
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
      $('#modalDestination').addClass('active');
    } else {
      $('#modalDestination').removeClass('active');
    }
  },

  openModal() {
    let isOpen = true;
    run.next(this, function() {
      this.updateModal(isOpen);
    });
  },

  closeModal() {
    let isOpen = false;
    run.next(this, function() {
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
