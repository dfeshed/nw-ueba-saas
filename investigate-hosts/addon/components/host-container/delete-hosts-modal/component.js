import Component from 'ember-component';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';
import { getPageOfMachines, deleteHosts } from 'investigate-hosts/actions/data-creators/host';
import { toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

const dispatchToActions = {
  toggleDeleteHostsModal,
  getPageOfMachines,
  deleteHosts
};

const stateToComputed = ({ endpoint: { machines } }) => ({
  selectedHostList: machines.selectedHostList
});

const DeleteHostsModal = Component.extend({
  flashMessage: injectService(),
  eventBus: injectService(),

  actions: {
    handleDeleteHosts() {
      const callBackOptions = {
        onSuccess: () => {
          this.get('flashMessage').showFlashMessage('investigateHosts.hosts.deleteHosts.success');
        },
        onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
      };
      this.send('deleteHosts', callBackOptions);
      this.send('closeDeleteHostsModal');
    },
    closeDeleteHostsModal() {
      this.send('toggleDeleteHostsModal');
      this.get('eventBus').trigger('rsa-application-modal-close-delete-hosts');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DeleteHostsModal);
