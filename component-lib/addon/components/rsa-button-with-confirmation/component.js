import Component from '@ember/component';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';

import layout from './template';

/**
 * RSA form button with confirmation modal.
 * @public
 */
export default Component.extend({

  layout,

  eventBus: service(),

  style: 'standard',

  isActive: false,

  isDisabled: false,

  isIconOnly: false,

  _showConfirmationModal: false,

  confirmationMessage: null,

  noteMessage: null,

  title: null,

  /**
   * Passed down function, which is to be called on clicking the ok button on confirmation modal
   * @public
   */
  onConfirm: null,


  _closeModal() {
    this.set('_showConfirmationModal', true);
    this.get('eventBus').trigger('rsa-application-modal-close-confirm-modal');
  },

  actions: {
    deleteAction() {
      this.toggleProperty('_showConfirmationModal');
      next(() => {
        this.get('eventBus').trigger('rsa-application-modal-open-confirm-modal');
      });
    },

    closeConfirmModal() {
      this._closeModal();
    },

    confirmAction() {
      this.sendAction('onConfirm');
      this._closeModal();
    },

    onModalClose() {
      this.set('_showConfirmationModal', false);
    }
  }
});

