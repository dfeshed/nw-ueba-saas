import Component from 'ember-component';
import { connect } from 'ember-redux';
import { getHostTableColumns } from 'investigate-hosts/reducers/schema/selectors';
import { getNextMachines, setHostColumnSort } from 'investigate-hosts/actions/data-creators/host';
import { processedHostList, serviceList, hostCountForDisplay } from 'investigate-hosts/reducers/hosts/selectors';
import { isValidExpression } from 'investigate-hosts/reducers/filters/selectors';
import {
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost
} from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  hostList: processedHostList(state),
  serviceList: serviceList(state),
  columns: getHostTableColumns(state),
  hostTotal: hostCountForDisplay(state), // Total number of hosts in search result
  hostFetchStatus: state.endpoint.machines.hostFetchStatus,
  loadMoreHostStatus: state.endpoint.machines.loadMoreHostStatus,
  isValidExpression: isValidExpression(state)
});

const dispatchToActions = {
  getNextMachines,
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost,
  setHostColumnSort
};

const HostTable = Component.extend({

  tagName: 'box',

  classNames: 'machine-zone'

});
export default connect(stateToComputed, dispatchToActions)(HostTable);
