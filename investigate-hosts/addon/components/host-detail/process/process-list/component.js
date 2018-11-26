import Component from '@ember/component';
import { connect } from 'ember-redux';
import { processList, areAllSelected } from 'investigate-hosts/reducers/details/process/selectors';
import {
  setRowIndex,
  getProcessDetails,
  sortBy,
  toggleProcessSelection,
  selectAllProcess,
  deSelectAllProcess,
  toggleProcessDetailsView } from 'investigate-hosts/actions/data-creators/process';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { machineOsType, hostName } from 'investigate-hosts/reducers/details/overview/selectors';
import CONFIG from './process-list-config';

const dispatchToActions = {
  getProcessDetails,
  sortBy,
  toggleProcessSelection,
  selectAllProcess,
  deSelectAllProcess,
  setRowIndex,
  toggleProcessDetailsView
};

const stateToComputed = (state) => ({
  processList: processList(state),
  sortField: state.endpoint.process.sortField,
  isDescOrder: state.endpoint.process.isDescOrder,
  isProcessTreeLoading: state.endpoint.process.isProcessTreeLoading,
  serviceList: serviceList(state),
  agentId: state.endpoint.detailsInput.agentId,
  osType: machineOsType(state),
  hostName: hostName(state),
  selectedProcessList: state.endpoint.process.selectedProcessList,
  areAllSelected: areAllSelected(state),
  selectedRowIndex: state.endpoint.process.selectedRowIndex
});

const ListComponent = Component.extend({

  tagName: 'box',

  columnsConfig: CONFIG,

  actions: {
    /**
     * Handle for the row click action
     * @param item
     * @param index
     * @public
     */
    handleRowClickAction(item, index) {
      const { pid } = item;
      if (this.get('selectedRowIndex') !== index) {
        this.send('setRowIndex', index);
        if (this.openPropertyPanel) {
          this.openPropertyPanel();
        }
        this.send('getProcessDetails', pid);
      } else {
        this.send('setRowIndex', null);
        if (this.closePropertyPanel) {
          this.closePropertyPanel();
        }
      }
    },

    sort(column) {
      const { field: sortField, isDescending: isDescOrder } = column;
      this.send('sortBy', sortField, !isDescOrder);
      column.set('isDescending', !isDescOrder);
    },

    toggleAllSelection() {
      if (!this.get('areAllSelected')) {
        this.send('selectAllProcess');
      } else {
        this.send('deSelectAllProcess');
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ListComponent);
