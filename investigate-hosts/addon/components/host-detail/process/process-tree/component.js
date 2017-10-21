import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { updateRowVisibility } from './utils';
import { processTree } from 'investigate-hosts/reducers/details/process/selectors';
import { getProcessDetails } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  getProcessDetails
};

const stateToComputed = (state) => ({
  treeAsList: processTree(state)
});

const TreeComponent = Component.extend({

  tagName: 'box',

  classNames: ['rsa-process-tree'],

  /**
   * Column configuration for the process list, displaying only process name and process ID
   * @type [Object]
   * @public
   */
  columnsConfig: [
    {
      field: 'name',
      width: 265,
      title: 'investigateHosts.process.processName',
      componentClass: 'host-detail/process/process-tree/process-name'
    },
    {
      field: 'pid',
      width: 65,
      title: 'investigateHosts.process.pid'
    }
  ],

  /**
   * Filtering the the items based on visible property, hiding the virtual child element based the parent expanded or not
   * @param items
   * @public
   */
  @computed('treeAsList.@each.visible')
  visibleItems() {
    return this.get('treeAsList').filterBy('visible', true);
  },

  actions: {
    handleToggleExpand(index, level, item) {
      const rows = this.get('treeAsList');
      const { pid, expanded } = item;
      updateRowVisibility(rows, pid, expanded);
    },
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
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(TreeComponent);
