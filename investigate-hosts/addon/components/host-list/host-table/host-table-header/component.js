import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';
import { connect } from 'ember-redux';
import { updateColumnVisibility, setHostColumnSort } from 'investigate-hosts/actions/data-creators/host';
import { isAllHostSelected } from 'investigate-hosts/reducers/hosts/selectors';
import { selectAllHosts, deSelectAllHosts } from 'investigate-hosts/actions/ui-state-creators';

const dispatchToActions = {
  updateColumnVisibility,
  selectAllHosts,
  deSelectAllHosts,
  setHostColumnSort
};

const stateToComputed = (state) => ({
  isAllHostSelected: isAllHostSelected(state)
});
const tableHeader = RSADataTableHeader.extend({

  actions: {
    toggleColumn(column) {
      column.toggleProperty('selected');
      this.send('updateColumnVisibility', column);
    },
    toggleAllSelection() {
      if (!this.get('isAllHostSelected')) {
        this.send('selectAllHosts');
      } else {
        this.send('deSelectAllHosts');
      }
    },
    sort(column) {
      if (column.isDescending !== undefined && !column.isDescending) {
        column.set('isDescending', true);
      } else {
        column.set('isDescending', false);
      }

      const { field: key, isDescending: descending } = column;

      this.send('setHostColumnSort', { key, descending });
    },
    clearSearchTerm() {
      this.set('searchTerm', '');
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(tableHeader);
