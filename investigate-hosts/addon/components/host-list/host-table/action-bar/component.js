import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import {
  noHostsSelected,
  allAreEcatAgents } from 'investigate-hosts/reducers/hosts/selectors';
import { toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

import { deleteHosts, getPageOfMachines, triggerMachineActions } from 'investigate-hosts/actions/data-creators/host';
import { setEndpointServer } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';

const stateToComputed = (state) => ({
  totalItems: state.endpoint.machines.totalItems,
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
  getPageOfMachines,
  resetRiskScore
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
    },

    handleServiceSelection(service) {
      this.send('setEndpointServer', true, service, triggerMachineActions);
      if (this.closeProperties) {
        this.closeProperties();
      }
    },

    handleResetHostsRiskScore(selectedHostList) {
      const limitedHostList = selectedHostList.slice(0, 100);
      const callBackOptions = {
        onSuccess: (response) => {
          const { data } = response;
          if (data === limitedHostList.length) {
            success('investigateHosts.hosts.resetHosts.success');
          } else {
            warning('investigateHosts.hosts.resetHosts.warning');
          }
        },
        onFailure: () => failure('investigateHosts.hosts.resetHosts.error')
      };
      this.send('resetRiskScore', limitedHostList, callBackOptions);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ActionBar);
