import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getHostTableColumns } from 'investigate-hosts/reducers/schema/selectors';
import { getNextMachines, setHostColumnSort, fetchHostContext, toggleRiskPanel } from 'investigate-hosts/actions/data-creators/host';
import {
  processedHostList,
  serviceList,
  hostCountForDisplay
} from 'investigate-hosts/reducers/hosts/selectors';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import { inject as service } from '@ember/service';

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
  showRiskPanel: state.endpoint.visuals.showRiskPanel
});

const dispatchToActions = {
  getNextMachines,
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost,
  setHostColumnSort,
  fetchHostContext,
  toggleRiskPanel
};

const HostTable = Component.extend({

  tagName: 'box',

  classNames: 'machine-zone',

  features: service(),

  @computed('columns')
  updatedColumns(columns) {
    const fixedColumns = columns.slice(0, 2); // checkbox and machine name should be displayed first and second
    const nonFixedColumns = columns.slice(2); // Remaining column sort by title
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
      if (this.get('features.rsaEndpointFusion')) {
        const { target: { classList } } = e;
        if (classList.contains('rsa-icon-expand-6-filled') ||
          classList.contains('rsa-form-checkbox-label') ||
          classList.contains('rsa-form-checkbox')) {
          e.stopPropagation();
        } else {
          const isRiskPanelVisible = this.get('showRiskPanel');
          const isSameRowClicked = table.get('selectedIndex') === index;
          if (isSameRowClicked && isRiskPanelVisible) {
            this.send('toggleRiskPanel', false);
          } else {
            this.send('toggleRiskPanel', true);
            this.send('fetchHostContext', item.machine.machineName);
          }
          table.set('selectedIndex', index);
        }
      }
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(HostTable);
