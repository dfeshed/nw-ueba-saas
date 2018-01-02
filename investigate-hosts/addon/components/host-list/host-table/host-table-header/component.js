import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';
import { connect } from 'ember-redux';
import { updateColumnVisibility, setHostColumnSort } from 'investigate-hosts/actions/data-creators/host';
import { isAllHostSelected } from 'investigate-hosts/reducers/hosts/selectors';
import { selectAllHosts, deSelectAllHosts } from 'investigate-hosts/actions/ui-state-creators';
import { capitalize } from 'ember-string';
import computed from 'ember-computed-decorators';

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
  /**
   * Search the filter control based on user entered text
   * @public
   */
  @computed('table.sortedColumns', 'searchTerm')
  filterList(allFilters, searchTerm) {
    const list = [ ...allFilters ];
    if (searchTerm && searchTerm.length > 3) {
      return list.filter((item) => {
        const name = this.get('i18n').t(item.title) || '';
        return capitalize(name.toString()).includes(capitalize(searchTerm));
      });
    }
    return list;
  },

  actions: {
    toggleColumn(column) {
      column.toggleProperty('selected');
      const { field, selected } = column.getProperties('field', 'selected');
      this.send('updateColumnVisibility', { field, selected });
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
