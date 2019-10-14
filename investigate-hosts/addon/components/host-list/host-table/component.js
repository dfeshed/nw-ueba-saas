import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getHostTableColumns } from 'investigate-hosts/reducers/schema/selectors';
import {
  getNextMachines,
  setHostColumnSort,
  fetchHostContext,
  onHostSelection,
  setFocusedHostIndex,
  saveColumnConfig
} from 'investigate-hosts/actions/data-creators/host';
import {
  processedHostList,
  serviceList,
  hostTotalLabel,
  nextLoadCount,
  isScanStartButtonDisabled,
  isScanStopButtonDisabled,
  isAllHostSelected,
  isSortDescending,
  sortField
} from 'investigate-hosts/reducers/hosts/selectors';
import _ from 'lodash';
import { next } from '@ember/runloop';

import {
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost,
  deSelectAllHosts,
  selectAllHosts

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
  focusedHost: state.endpoint.machines.focusedHost,
  focusedHostIndex: state.endpoint.machines.focusedHostIndex,
  selections: state.endpoint.machines.selectedHostList || [],
  nextLoadCount: nextLoadCount(state),
  isScanStartButtonDisabled: isScanStartButtonDisabled(state),
  isScanStopButtonDisabled: isScanStopButtonDisabled(state),
  isAllHostSelected: isAllHostSelected(state),
  isSortDescending: isSortDescending(state),
  sortField: sortField(state)
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
  deSelectAllHosts,
  selectAllHosts,
  saveColumnConfig
};

const HostTable = Component.extend({

  tagName: 'box',

  classNames: 'machine-zone',

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

    /**
     * Abort the action if dragged column is machine name, risk score and checkbox also abort if column in dropped to
     * machine name, risk score and checkbox.
     *
     */
    onReorderColumns(columns, newColumns, column, fromIndex, toIndex) {

      return !(column.dataType === 'checkbox' ||
        column.field === 'machineIdentity.machineName' ||
        column.field === 'score' ||
        toIndex === 0 ||
        toIndex === 1 ||
        toIndex === 2);

    },

    toggleSelectedRow(item, index, e, table) {
      const { target: { classList } } = e;
      // If it's machine name click don't select the row
      if (e.target.tagName.toLowerCase() === 'a') {
        return;
      }
      // do not select row when checkbox is clicked
      if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
        const isSameRowClicked = table.get('selectedIndex') === index;
        const openProperties = this.get('openProperties');
        this.send('setFocusedHostIndex', index);

        if (!isSameRowClicked && openProperties) {
          // if clicked row is one among the checkbox selected list, row click will highlight that row keeping others
          // checkbox selected.
          // when a row not in the checkbox selected list is clicked, other checkboxes are cleared.
          if (!this.isAlreadySelected(this.get('selections'), item)) {
            this.send('deSelectAllHosts');
            this.send('toggleMachineSelected', item);
          }
          this.send('onHostSelection', item);
          next(() => {
            this.openProperties();
          });
        } else {
          this.send('toggleMachineSelected', item);
          this.closeProperties();
          this.send('setFocusedHostIndex', -1);
        }
      }
    },
    /* beforeContextMenuShow executes before the context menu is rendered.
      it alters the list of context menu items based on conditions like;
      a. Is MFT enabled
      b. Is the target element an anchor tag
      c. number of items selected */
    beforeContextMenuShow(menu, event) {
      const { contextSelection: item, contextItems } = menu;
      const { isMFTEnabled } = item;
      const selections = this.get('selections');

      // Need to store this locally set it back again to menu object
      if (contextItems.length) {
        if (!this.get('updatedContextConfBackup') && isMFTEnabled) {
          this.set('updatedContextConfBackup', contextItems);
        }
        if (!this.get('initialContextConfBackup') && !isMFTEnabled) {
          this.set('initialContextConfBackup', contextItems);
        }
      }

      // For anchor tag hid the context menu and show browser default right click menu
      if (event.target.tagName.toLowerCase() === 'a') {
        menu.set('contextItems', []);
      } else {
        if (isMFTEnabled && (selections.length <= 1)) {
          menu.set('contextItems', this.get('updatedContextConfBackup'));
        } else {
          menu.set('contextItems', this.get('initialContextConfBackup'));
        }

        // Highlight is removed and right panel is closed when right clicked on non-highlighted row
        if (this.get('focusedHost') && this.get('focusedHost').id !== item.id) {
          this.send('setFocusedHostIndex', -1);
          this.closeProperties();
        }
        if (!this.isAlreadySelected(selections, item)) {
          this.send('deSelectAllHosts');
          this.send('toggleMachineSelected', item);
        }
      }
    },
    toggleAllSelection() {
      if (!this.get('isAllHostSelected')) {
        this.send('selectAllHosts');
      } else {
        this.send('deSelectAllHosts');
      }
    },
    sort(columnSort) {
      if (this.closeProperties) {
        this.closeProperties();
      }
      this.send('setHostColumnSort', columnSort);
    },

    onColumnConfigChange(changedProperty, changedColumns) {
      this.send('saveColumnConfig', 'hosts', changedProperty, changedColumns);
    },
    onToggleColumn(column, columns) {
      this.send('saveColumnConfig', 'hosts', 'display', columns);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(HostTable);
