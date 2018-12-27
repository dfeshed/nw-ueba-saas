import Component from '@ember/component';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

export default Component.extend({

  classNames: ['scan-modal'],

  eventBus: service(),

  modalTitle: '',

  warningMessages: null,

  eventId: 'host-scan',

  onModalClose: null,

  primaryAction: null,

  @computed('modalStyle')
  style(modalStyle) {
    return `standard scan-modal ${modalStyle}`;
  },

  @computed('command')
  decorator(command) {
    const i18n = this.get('i18n');
    if (command === 'START_SCAN') {
      return {
        bodyMessage: i18n.t('investigateHosts.hosts.initiateScan.modal.quickScan.description'),
        primaryButtonText: i18n.t('investigateHosts.hosts.button.initiateScan')
      };
    }
    return {
      bodyMessage: i18n.t('investigateHosts.hosts.cancelScan.modal.description'),
      primaryButtonText: i18n.t('investigateHosts.hosts.button.cancelScan')
    };
  },

  actions: {

    handleClick() {
      this.send('closeModal');
      this.primaryAction(this.get('command'));
    },
    closeModal() {
      this.onModalClose();
    }
  }
});
