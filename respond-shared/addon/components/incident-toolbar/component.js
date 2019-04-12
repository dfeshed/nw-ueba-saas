import Component from '@ember/component';
import layout from './template';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';

export default Component.extend({
  layout,

  tagName: '',

  eventBus: service(),

  activeModalId: null,

  actions: {
    showModal(modalId) {
      this.set('activeModalId', modalId);
      next(() => {
        this.get('eventBus').trigger(`rsa-application-modal-open-${modalId}`);
      });
    },
    closeModal(modalId) {
      this.get('eventBus').trigger(`rsa-application-modal-close-${modalId}`);
      this.set('activeModalId', null);
    },
    createIncident() {
      this.send('showModal', 'create-incident');
    },
    addToIncident() {
      this.send('showModal', 'add-to-incident');
    }
  }

});
