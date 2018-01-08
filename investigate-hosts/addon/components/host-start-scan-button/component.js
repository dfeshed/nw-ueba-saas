import Component from 'ember-component';
import { next } from 'ember-runloop';
import injectService from 'ember-service/inject';
import { startScan } from 'investigate-hosts/actions/data-creators/host';

export default Component.extend({

  classNames: ['host-start-scan-button'],

  eventBus: injectService(),

  flashMessage: injectService(),

  isDisabled: false,

  style: '',

  isIconOnly: false,

  _showStartScanModal: false,

  buttonText: null,

  modalTitle: '',

  warningMessage: null,

  agentIds: null,

  _closeModal() {
    this.get('eventBus').trigger('rsa-application-modal-close-start-scan-modal');
    this.set('_showStartScanModal', false);
  },
  actions: {

    handleInitiateScan() {
      const callBackOptions = {
        onSuccess: () => this.get('flashMessage').showFlashMessage('investigateHosts.hosts.initiateScan.success'),
        onFailure: (message) => this.get('flashMessage').showErrorMessage(message)
      };
      startScan(this.get('agentIds'), callBackOptions);
      this.send('closeScanModal');
    },

    closeScanModal() {
      this._closeModal();
    },

    onModalClose() {
      this.set('_showStartScanModal', false);
    },

    toggleScanStartModal() {
      this.set('_showStartScanModal', true);
      next(() => {
        this.get('eventBus').trigger('rsa-application-modal-open-start-scan-modal');
      });
    }
  }
});
