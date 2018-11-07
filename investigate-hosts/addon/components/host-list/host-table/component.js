import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getHostTableColumns } from 'investigate-hosts/reducers/schema/selectors';
import { getNextMachines, setHostColumnSort, fetchHostContext } from 'investigate-hosts/actions/data-creators/host';
import {
  processedHostList,
  serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

import {
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost
} from 'investigate-hosts/actions/ui-state-creators';

import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state) => ({
  hostList: processedHostList(state),
  serviceList: serviceList(state),
  columns: getHostTableColumns(state),
  hostTotal: state.endpoint.machines.totalItems, // Total number of hosts in search result
  hostFetchStatus: state.endpoint.machines.hostFetchStatus,
  loadMoreHostStatus: state.endpoint.machines.loadMoreHostStatus,
  serverId: state.endpointQuery.serverId,
  selectedHostsCount: state.endpoint.machines.selectedHostList.length,
  serviceId: serviceId(state),
  timeRange: timeRange(state)
});

const dispatchToActions = {
  getNextMachines,
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost,
  setHostColumnSort,
  fetchHostContext
};

const HostTable = Component.extend({

  tagName: 'box',

  classNames: 'machine-zone',

  @computed('columns')
  updatedColumns(columns) {
    const fixedColumns = columns.slice(0, 3); // checkbox, machine name and risk score should be displayed first
    const nonFixedColumns = columns.slice(3); // Remaining column sort by title
    const sortedColumn = this._sortList(nonFixedColumns);
    return [...fixedColumns, ...sortedColumn];
  },

  _sortList(columnList) {
    const i18n = this.get('i18n');
    return _.sortBy(columnList, [(column) => {
      return i18n.t(column.title).toString();
    }]);
  },

  actions: {
    onRowSelection(item) {
      const entity = {
        entityType: 'HOST',
        entityId: item.machineIdentity.machineName
      };
      this.send('handleRowSelection', entity);
    },
    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(HostTable);
