import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { observer } from '@ember/object';
import CONFIG from './process-config';
import { connect } from 'ember-redux';
import { updateRowVisibility } from './utils';
import { processTree, areAllSelected } from 'investigate-hosts/reducers/details/process/selectors';
import {
  setRowIndex,
  getProcessDetails,
  toggleProcessSelection,
  selectAllProcess,
  deSelectAllProcess } from 'investigate-hosts/actions/data-creators/process';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { machineOsType, hostName } from 'investigate-hosts/reducers/details/overview/selectors';

const dispatchToActions = {
  getProcessDetails,
  toggleProcessSelection,
  selectAllProcess,
  deSelectAllProcess,
  setRowIndex
};

const stateToComputed = (state) => ({
  serviceList: serviceList(state),
  treeAsList: processTree(state),
  isProcessTreeLoading: state.endpoint.process.isProcessTreeLoading,
  selectedRowIndex: state.endpoint.process.selectedRowIndex,
  agentId: state.endpoint.detailsInput.agentId,
  selectedProcessList: state.endpoint.process.selectedProcessList,
  osType: machineOsType(state),
  hostName: hostName(state),
  areAllSelected: areAllSelected(state),
  isProcessDetailsView: state.endpoint.process.isProcessDetailsView
});

const TreeComponent = Component.extend({

  tagName: 'box',

  classNames: ['rsa-process-tree'],

  /**
   * Column configuration for the process list, displaying only process name and process ID
   * @type [Object]
   * @public
   */
  columnsConfig: CONFIG,

  /**
   * Filtering the the items based on visible property, hiding the virtual child element based the parent expanded or not
   * @param items
   * @public
   */
  @computed('treeAsList.@each.visible')
  visibleItems() {
    return this.get('treeAsList').filterBy('visible', true);
  },

  /**
   * Observer to dispatch getProcessdetails action when navigate to Process tab using explore
   * This is used to make a web socket call to get the first process details after filtering the process in the selector
   * @public
   */

  loadExploredProcessDetails: observer('treeAsList', function() {
    const treeList = this.get('treeAsList') || [];
    if (treeList.length) {
      this.send('getProcessDetails', treeList[0].pid);
    }
  }),

  actions: {
    handleToggleExpand(index, level, item) {
      const rows = this.get('treeAsList');
      const { pid, expanded } = item;
      updateRowVisibility(rows, pid, expanded);
    },

    toggleAllSelection() {
      if (!this.get('areAllSelected')) {
        this.send('selectAllProcess');
      } else {
        this.send('deSelectAllProcess');
      }
    },

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
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(TreeComponent);
