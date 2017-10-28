import Component from 'ember-component';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';
import { Machines } from 'investigate-hosts/actions/api';
import _ from 'lodash';
import { toggleCancelScanModal } from 'investigate-hosts/actions/ui-state-creators';

const dispatchToActions = {
  toggleCancelScanModal
};


const ActionBar = Component.extend({

  tagName: '',

  flashMessage: injectService(),

  eventBus: injectService(),

  actions: {

    handleCancelScan() {
      const agentIds = _.map(this.get('selectedHostList'), 'id');
      Machines.stopScanRequest({ agentIds, scanType: 'CANCEL_SCAN' })
        .then(() => {
          this.get('flashMessage').showFlashMessage('investigateHosts.hosts.cancelScan.success');
        }).catch(({ meta: message }) => {
          this.get('flashMessage').showErrorMessage(message.message);
        });
      this.send('closeCancelScanModal');
    },

    closeCancelScanModal() {
      this.send('toggleCancelScanModal');
      // This is required otherwise overlay not getting removed
      this.get('eventBus').trigger('rsa-application-modal-close-cancel-scan');
    }
  }

});
export default connect(undefined, dispatchToActions)(ActionBar);
