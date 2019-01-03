import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getHostTableColumns } from 'investigate-hosts/reducers/schema/selectors';
import { getNextMachines, setHostColumnSort, fetchHostContext, onHostSelection, setFocusedHostIndex }
  from 'investigate-hosts/actions/data-creators/host';
import {
  processedHostList,
  serviceList,
  hostTotalLabel } from 'investigate-hosts/reducers/hosts/selectors';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import { next } from '@ember/runloop';

import {
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost,
  deSelectAllHosts
} from 'investigate-hosts/actions/ui-state-creators';

import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state) => ({
  hostList: processedHostList(state),
  serviceList: serviceList(state),
  columns: getHostTableColumns(state),
  hostTotal: hostTotalLabel(state), // Total number of hosts in search result
  hostFetchStatus: state.endpoint.machines.hostFetchStatus,
  loadMoreHostStatus: state.endpoint.machines.loadMoreHostStatus,
  serverId: state.endpointQuery.serverId,
  selectedHostsCount: state.endpoint.machines.selectedHostList.length,
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  servers: state.endpointServer.serviceData,
  focusedHostIndex: state.endpoint.machines.focusedHostIndex,
  selections: state.endpoint.machines.selectedHostList || []
});

const dispatchToActions = {
  getNextMachines,
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost,
  setHostColumnSort,
  fetchHostContext,
  onHostSelection,
  setFocusedHostIndex,
  deSelectAllHosts
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

  isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('id', item.id);
    }
    return selected;
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
      const { target: { classList } } = e;

      // do not select row when checkbox is clicked
      if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
        const isSameRowClicked = table.get('selectedIndex') === index;
        const openProperties = this.get('openProperties');
        this.send('setFocusedHostIndex', index);

        if (!isSameRowClicked && openProperties) {
          this.send('deSelectAllHosts');
          this.send('toggleMachineSelected', item);
          this.send('onHostSelection', item);
          next(() => {
            this.openProperties();
          });
        } else {
          this.send('toggleMachineSelected', item);
          this.closeProperties();
          this.send('setFocusedHostIndex', null);
        }
      }
    },

    beforeContextMenuShow(menu, event) {
      const { contextSelection: item, contextItems } = menu;
      this.send('setFocusedHostIndex', null);
      if (!this.get('contextItems')) {
        // Need to store this locally set it back again to menu object
        this.set('contextItems', contextItems);
      }
      // For anchor tag hid the context menu and show browser default right click menu
      if (event.target.tagName.toLowerCase() === 'a') {
        menu.set('contextItems', []);
      } else {
        menu.set('contextItems', this.get('contextItems'));
        this.closeProperties();
        if (!this.isAlreadySelected(this.get('selections'), item)) {
          this.send('deSelectAllHosts');
          this.send('toggleMachineSelected', item);
        }
      }
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(HostTable);
