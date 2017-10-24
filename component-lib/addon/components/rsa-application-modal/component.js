import $ from 'jquery';
import Component from 'ember-component';
import { next, schedule } from 'ember-runloop';
import service from 'ember-service/inject';
import layout from './template';

export default Component.extend({
  layout,

  eventBus: service(),
  contextualHelp: service(),

  classNames: ['rsa-application-modal'],

  classNameBindings: [
    'isOpen',
    'style'],

  eventId: null,

  isOpen: false,

  style: 'standard', // ['standard', 'error']

  label: null,

  autoOpen: false,

  onClose: null,

  init() {
    this.listen();
    this._super(arguments);
  },

  didInsertElement() {
    schedule('afterRender', () => {
      this.$('.modal-close').on('click', () => {
        next(() => this.closeModal());
      });
      if (this.get('autoOpen')) {
        this.openModal();
      }
    });
  },

  willDestroyElement() {
    if (this.get('isOpen')) {
      this.closeModal();
    }
    this.get('eventBus').off(`rsa-application-modal-open-${this.get('eventId')}`, this, 'openModal');
    this.get('eventBus').off(`rsa-application-modal-close-${this.get('eventId')}`, this, 'closeModal');
    this.get('eventBus').off('rsa-application-modal-close-all', this, 'closeModal');
  },

  listen() {
    this.get('eventBus').on(`rsa-application-modal-open-${this.get('eventId')}`, this, 'openModal');
    this.get('eventBus').on(`rsa-application-modal-close-${this.get('eventId')}`, this, 'closeModal');
    this.get('eventBus').on('rsa-application-modal-close-all', this, 'closeModal');
  },

  updateModal(truth) {
    next(() => {
      if (truth) {
        this.get('eventBus').trigger('rsa-application-modal-did-open');
        $('#modalDestination').addClass('active');
        this.sendAction('modalDidOpen');
      } else {
        this.get('eventBus').trigger('rsa-application-modal-did-close');
        $('#modalDestination').removeClass('active');
        this.sendAction('modalDidClose');
      }

      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.set('isOpen', truth);
      }
    });
  },

  openModal() {
    const isOpen = true;
    next(this, function() {
      this.updateModal(isOpen);
    });
  },

  closeModal() {
    const isOpen = false;
    next(this, function() {
      this.updateModal(isOpen);
    });
    if (this.get('onClose')) {
      this.get('onClose')();
    }
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
    },

    goToHelp(module, topic) {
      this.get('contextualHelp').goToHelp(module, topic);
    }
  }

});
