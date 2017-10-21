import Component from 'ember-component';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';
import { Machines } from 'investigate-hosts/actions/api';
import { toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

const dispatchToActions = {
  toggleDeleteHostsModal
};

const stateToComputed = ({ endpoint: { machines } }) => ({
  selectedHostList: machines.selectedHostList
});

const DeleteHostsModal = Component.extend({
  flashMessage: injectService(),
  eventBus: injectService(),

  actions: {
    handleDeleteHosts() {
      Machines.deleteHosts(this.get('selectedHostList'))
        .then(() => {
          this.get('flashMessage').showFlashMessage('investigateHosts.hosts.deleteHosts.success');
        })
        .catch(() => {
          this.get('flashMessage').showFlashMessage('investigateHosts.hosts.deleteHosts.error');
        });
      this.send('closeDeleteHostsModal');
    },
    closeDeleteHostsModal() {
      this.send('toggleDeleteHostsModal');
      this.get('eventBus').trigger('rsa-application-modal-close-delete-hosts');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DeleteHostsModal);
