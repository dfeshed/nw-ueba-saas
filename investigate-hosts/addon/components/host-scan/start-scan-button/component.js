import Component from 'ember-component';
import injectService from 'ember-service/inject';
import { startScan } from 'investigate-hosts/actions/data-creators/host';

export default Component.extend({

  classNames: ['host-start-scan-button'],

  flashMessage: injectService(),

  isDisabled: false,

  isIconOnly: false,

  _showStartScanModal: false,

  buttonText: null,

  modalTitle: '',

  warningMessages: null,

  agentIds: null,

  actions: {

    handleInitiateScan() {
      const callBackOptions = {
        onSuccess: () => this.get('flashMessage').showFlashMessage('investigateHosts.hosts.initiateScan.success'),
        onFailure: (message) => this.get('flashMessage').showErrorMessage(message)
      };
      startScan(this.get('agentIds'), callBackOptions);
    },

    onModalClose() {
      this.set('_showStartScanModal', false);
    },

    toggleScanStartModal() {
      this.set('_showStartScanModal', true);
    }
  }
});
