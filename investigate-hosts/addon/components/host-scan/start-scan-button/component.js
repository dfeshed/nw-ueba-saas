import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { startScan } from 'investigate-hosts/actions/data-creators/host';
import { success, failure } from 'investigate-shared/utils/flash-messages';

export default Component.extend({

  classNames: ['host-start-scan-button'],

  flashMessage: service(),

  isDisabled: false,

  isIconOnly: false,

  _showStartScanModal: false,

  buttonText: null,

  modalTitle: '',

  warningMessages: null,

  agentIds: null,

  title: null,

  actions: {

    handleInitiateScan() {
      const callBackOptions = {
        onSuccess: () => {
          success('investigateHosts.hosts.initiateScan.success');
        },
        onFailure: () => {
          failure('investigateHosts.hosts.initiateScan.failure');
        }
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
