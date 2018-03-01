import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { stopScan } from 'investigate-hosts/actions/data-creators/host';

export default Component.extend({

  classNames: 'stop-scan-button',

  flashMessage: service(),

  buttonText: '',

  isDisabled: false,

  isIconOnly: false,

  _showStopScanModal: false,

  modalTitle: '',

  warningMessage: null,

  agentIds: null,

  actions: {

    handleStopScan() {
      const callBackOptions = {
        onSuccess: () => this.get('flashMessage').showFlashMessage('investigateHosts.hosts.cancelScan.success'),
        onFailure: (message) => this.get('flashMessage').showErrorMessage(message)
      };
      stopScan(this.get('agentIds'), callBackOptions);
    },

    toggleStopScanModal() {
      this.set('_showStopScanModal', true);
    },

    onModalClose() {
      this.set('_showStopScanModal', false);
    }
  }
});
