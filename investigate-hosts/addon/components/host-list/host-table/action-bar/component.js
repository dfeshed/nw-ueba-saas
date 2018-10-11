import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import {
  noHostsSelected,
  hostCountForDisplay,
  allAreEcatAgents } from 'investigate-hosts/reducers/hosts/selectors';
import { toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

import { deleteHosts, getPageOfMachines } from 'investigate-hosts/actions/data-creators/host';
import { setEndpointServer } from 'investigate-hosts/actions/data-creators/endpoint-server';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';

const stateToComputed = (state) => ({
  totalItems: hostCountForDisplay(state),
  noHostsSelected: noHostsSelected(state),
  allAreEcatAgents: allAreEcatAgents(state),
  selectedHostList: state.endpoint.machines.selectedHostList,
  serverId: state.endpointQuery.serverId,
  servers: state.endpointServer,
  selectedFilterId: selectedFilterId(state.endpoint),
  savedFilter: savedFilter(state.endpoint),
  hostFilters: state.endpoint.filter.savedFilterList
});

const dispatchToActions = {
  toggleDeleteHostsModal,
  deleteHosts,
  setEndpointServer,
  getPageOfMachines
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
