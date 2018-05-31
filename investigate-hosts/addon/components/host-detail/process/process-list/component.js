import Component from '@ember/component';
import { connect } from 'ember-redux';
import { processList } from 'investigate-hosts/reducers/details/process/selectors';
import { getProcessDetails, sortBy } from 'investigate-hosts/actions/data-creators/process';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { machineOsType, hostName } from 'investigate-hosts/reducers/details/overview/selectors';

const dispatchToActions = {
  getProcessDetails,
  sortBy
};

const stateToComputed = (state) => ({
  processList: processList(state),
  sortField: state.endpoint.process.sortField,
  isDescOrder: state.endpoint.process.isDescOrder,
  isProcessTreeLoading: state.endpoint.process.isProcessTreeLoading,
  serviceList: serviceList(state),
  agentId: state.endpoint.detailsInput.agentId,
  osType: machineOsType(state),
  hostName: hostName(state)
});

const ListComponent = Component.extend({

  tagName: 'box',

  columnsConfig: [
    {
      'field': 'name',
      'title': 'investigateHosts.process.processName',
      'dataType': 'STRING',
      'defaultProjection': true,
      'wrapperType': 'STRING',
      'width': 265,
      'isDescending': false
    },
    {
      'field': 'pid',
      'title': 'investigateHosts.process.pid',
      'dataType': 'STRING',
      'defaultProjection': true,
      'wrapperType': 'STRING',
      'width': 65,
      'isDescending': false
    }
  ],

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
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ListComponent);
