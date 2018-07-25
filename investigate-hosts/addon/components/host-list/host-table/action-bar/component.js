import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import {
  noHostsSelected,
  hostCountForDisplay,
  allAreEcatAgents } from 'investigate-hosts/reducers/hosts/selectors';
import { toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

import { deleteHosts } from 'investigate-hosts/actions/data-creators/host';


const stateToComputed = (state) => ({
  totalItems: hostCountForDisplay(state),
  noHostsSelected: noHostsSelected(state),
  allAreEcatAgents: allAreEcatAgents(state),
  selectedHostList: state.endpoint.machines.selectedHostList
});

const dispatchToActions = {
  toggleDeleteHostsModal,
  deleteHosts
};

const ActionBar = Component.extend({

  tagName: 'section',

  classNames: 'host-table__toolbar',

  flashMessage: service(),

  i18n: service(),

  actions: {
    handleDeleteHosts() {
      const callBackOptions = {
        onSuccess: () => {
          this.get('flashMessage').showFlashMessage('investigateHosts.hosts.deleteHosts.success');
        },
        onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
      };
      this.send('deleteHosts', callBackOptions);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ActionBar);
