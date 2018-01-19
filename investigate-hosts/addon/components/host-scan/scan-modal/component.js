import Component from 'ember-component';
import injectService from 'ember-service/inject';
import computed from 'ember-computed-decorators';

export default Component.extend({

  classNames: 'scan-modal',

  eventBus: injectService(),

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
      this.primaryAction();
    },
    closeModal() {
      this.onModalClose();
      this.get('eventBus').trigger(`rsa-application-modal-close-${this.get('eventId')}`);
    }
  }
});
