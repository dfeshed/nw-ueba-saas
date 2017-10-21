import Component from 'ember-component';
import { connect } from 'ember-redux';
import { getHostTableColumns } from 'investigate-hosts/reducers/schema/selectors';
import { getNextMachines, setHostColumnSort } from 'investigate-hosts/actions/data-creators/host';
import { processedHostList } from 'investigate-hosts/reducers/hosts/selectors';
import {
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost
} from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  hostList: processedHostList(state),
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

  classNames: 'machine-zone',

  didRender() {
    this._super(...arguments);
    const rsaDataTableBody = this.$('.rsa-data-table-body');
    if (rsaDataTableBody.length !== 0) {

      // To make the rsa-data-table-load-more button at center of the screen, even if horizontal scroll moves
      const initialWidth = (rsaDataTableBody[0].clientWidth) / 2;
      rsaDataTableBody.scroll(() => {
        const scrolledWidth = rsaDataTableBody.scrollLeft();
        this.$('.rsa-data-table-load-more.rsa-hosts-load-more').css('left', initialWidth + scrolledWidth);
      });

      // Increase the rsa-data-table-body-rows border dynamically
      const dataTableTotalWidth = rsaDataTableBody[0].scrollWidth;
      this.$('.rsa-data-table-body-rows').innerWidth(dataTableTotalWidth);
    }
  },

  actions: {
    handleRowClick({ id }) {
      this.send('toggleMachineSelected', id);
    }
  }

});
export default connect(stateToComputed, dispatchToActions)(HostTable);
