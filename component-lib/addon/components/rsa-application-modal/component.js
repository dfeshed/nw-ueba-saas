import Component from '@ember/component';
import { next, schedule } from '@ember/runloop';
import { inject as service } from '@ember/service';
import layout from './template';

export default Component.extend({
  layout,

  eventBus: service(),
  contextualHelp: service(),

  classNames: ['rsa-application-modal'],

  classNameBindings: [
    'isOpen',
    'style'
  ],

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
      if (this.get('autoOpen')) {
        this.openModal();
      }

      // clear inlinestyles that get appended and break the cursor styles
      // https://bedfordjira.na.rsa.net/browse/ASOC-77271
      this.get('element').setAttribute('style', '');
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
      const modalDest = document.querySelector('#modalDestination');
      if (modalDest) {
        if (truth) {
          modalDest.classList.add('active');
        } else {
          modalDest.classList.remove('active');
        }
      }

      if (this.isDestroyed || this.isDestroying) {
        return;
      }

      if (truth) {
        this.get('eventBus').trigger('rsa-application-modal-did-open');
        if (this.modalDidOpen) {
          this.modalDidOpen();
        }
      } else {
        this.get('eventBus').trigger('rsa-application-modal-did-close');
        if (this.modalDidClose) {
          this.modalDidClose();
        }
      }

      this.set('isOpen', truth);
    });
  },

  openModal() {
    const isOpen = true;
    next(() => {
      this.updateModal(isOpen);
    });
  },

  closeModal() {
    const isOpen = false;
    next(() => {
      this.updateModal(isOpen);
    });
    if (this.get('onClose')) {
      this.get('onClose')();
      this.set('onClose', null);
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