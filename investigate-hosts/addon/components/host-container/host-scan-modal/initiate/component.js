import Component from 'ember-component';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';
import { Machines } from 'investigate-hosts/actions/api';
import { toggleInitiateScanModal } from 'investigate-hosts/actions/ui-state-creators';
import { areAnyEcatAgents } from 'investigate-hosts/reducers/hosts/selectors';
import { getSelectedAgentIds } from 'investigate-hosts/util/util';

const stateToComputed = (state) => ({
  areAnyEcatAgents: areAnyEcatAgents(state)
});

const dispatchToActions = {
  toggleInitiateScanModal
};

const InitiateModal = Component.extend({

  tagName: 'div',

  flashMessage: injectService(),

  eventBus: injectService(),

  eventId: 'initiate-scan',

  selectedHostList: null,

  /**
   * set to true when some of the selected hosts are already being scanning
   * @public
   */
  showScanningMessage: false,

  actions: {

    handleInitiateScan() {
      // Invoking the api to start the scans
      const agentIds = getSelectedAgentIds(this.get('selectedHostList'));
      Machines.startScanRequest(agentIds)
        .then(() => {
          this.get('flashMessage').showFlashMessage('investigateHosts.hosts.initiateScan.success');
        }).catch(({ meta: message }) => {
          this.get('flashMessage').showErrorMessage(message.message);
        });
      this.send('closeInitiateScanModal');
    },

    closeInitiateScanModal() {
      this.send('toggleInitiateScanModal');
      // This is required otherwise overlay not getting removed
      this.get('eventBus').trigger('rsa-application-modal-close-initiate-scan');
    }
  }

});
export default connect(stateToComputed, dispatchToActions)(InitiateModal);
