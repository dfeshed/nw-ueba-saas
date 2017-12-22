import Component from 'ember-component';
import { connect } from 'ember-redux';
import { getHostTableColumns } from 'investigate-hosts/reducers/schema/selectors';
import { getNextMachines, setHostColumnSort } from 'investigate-hosts/actions/data-creators/host';
import { processedHostList, serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import {
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost
} from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  hostList: processedHostList(state),
  serviceList: serviceList(state),
  columns: getHostTableColumns(state),
  hostFetchStatus: state.endpoint.machines.hostFetchStatus,
  totalItems: state.endpoint.machines.totalItems,
  loadMoreHostStatus: state.endpoint.machines.loadMoreHostStatus
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
