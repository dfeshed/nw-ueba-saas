import Component from '@ember/component';
import { connect } from 'ember-redux';
import { processList, areAllSelected } from 'investigate-hosts/reducers/details/process/selectors';
import { getProcessDetails, sortBy, toggleProcessSelection, selectAllProcess, deSelectAllProcess } from 'investigate-hosts/actions/data-creators/process';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { machineOsType, hostName } from 'investigate-hosts/reducers/details/overview/selectors';
import CONFIG from './process-list-config';

const dispatchToActions = {
  getProcessDetails,
  sortBy,
  toggleProcessSelection,
  selectAllProcess,
  deSelectAllProcess
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
  areAllSelected: areAllSelected(state)
});

const ListComponent = Component.extend({

  tagName: 'box',

  columnsConfig: CONFIG,

  actions: {
    /**
     * Handle for the row click action
     * @param item
     * @param index
     * @param e
     * @param table
     * @public
     */
    handleRowClickAction(item, index, e, table) {
      const { pid } = item;
      table.set('selectedIndex', index);
      this.send('getProcessDetails', pid);
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
