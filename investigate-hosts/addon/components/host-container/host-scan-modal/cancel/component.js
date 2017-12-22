import Component from 'ember-component';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';
import { Machines } from 'investigate-hosts/actions/api';
import { toggleCancelScanModal } from 'investigate-hosts/actions/ui-state-creators';
import { areAnyEcatAgents } from 'investigate-hosts/reducers/hosts/selectors';
import { getSelectedAgentIds } from 'investigate-hosts/util/util';

const stateToComputed = (state) => ({
  areAnyEcatAgents: areAnyEcatAgents(state)
});

const dispatchToActions = {
  toggleCancelScanModal
};

const ActionBar = Component.extend({

  tagName: '',

  flashMessage: injectService(),

  eventBus: injectService(),

  actions: {

    handleCancelScan() {
      const agentIds = getSelectedAgentIds(this.get('selectedHostList'));

      Machines.stopScanRequest(agentIds)
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
export default connect(stateToComputed, dispatchToActions)(ActionBar);
