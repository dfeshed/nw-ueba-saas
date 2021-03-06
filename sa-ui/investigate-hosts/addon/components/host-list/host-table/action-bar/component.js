import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { noHostsSelected, isScanStartButtonDisabled, actionsDisableMessage, isScanStopButtonDisabled } from 'investigate-hosts/reducers/hosts/selectors';
import { toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

import { deleteHosts, getPageOfMachines, changeEndpointServerSelection } from 'investigate-hosts/actions/data-creators/host';
import { setEndpointServer } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';

const stateToComputed = (state) => ({
  totalItems: state.endpoint.machines.totalItems,
  noHostsSelected: noHostsSelected(state),
  isScanStartButtonDisabled: isScanStartButtonDisabled(state),
  isScanStopButtonDisabled: isScanStopButtonDisabled(state),
  selectedHostList: state.endpoint.machines.selectedHostList,
  serverId: state.endpointQuery.serverId,
  servers: state.endpointServer,
  selections: state.endpoint.machines.selectedHostList || [],
  actionsDisableMessage: actionsDisableMessage(state)
});
const noop = () => {};

const dispatchToActions = {
  toggleDeleteHostsModal,
  deleteHosts,
  setEndpointServer,
  getPageOfMachines,
  resetRiskScore,
  changeEndpointServerSelection
};

@classic
@tagName('section')
@classNames('host-table__toolbar')
class ActionBar extends Component {
  @service
  flashMessage;

  @service
  i18n;

  pivotToInvestigate = noop;
  openFilterPanel = noop;
  showConfirmationModal = noop;
  showScanModal = noop;

  @action
  handleDeleteHosts() {
    const callBackOptions = {
      onSuccess: () => {
        this.get('flashMessage').showFlashMessage('investigateHosts.hosts.deleteHosts.success');
      },
      onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
    };
    this.send('deleteHosts', callBackOptions);
  }

  @action
  handleServiceSelection(service) {
    this.send('changeEndpointServerSelection', service);
    if (this.closeProperties) {
      this.closeProperties();
    }
  }
}

export default connect(stateToComputed, dispatchToActions)(ActionBar);
