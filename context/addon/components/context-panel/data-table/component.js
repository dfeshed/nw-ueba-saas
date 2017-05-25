import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import { getData } from 'context/util/context-data-modifier';
import set from 'ember-metal/set';

const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  dataSources: context.dataSources
});

const DataTableComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__context-data-table',

  @computed('contextData', 'lookupData.[]', 'dSDetails')
  getDataSourceData: (contextData, [lookupData], dSDetails) => contextData ? contextData.data : getData(lookupData, dSDetails),

  @computed('getDataSourceData', 'currentSort.field', 'currentSort.direction')
  getSortedData: (getDataSourceData, field, direction) => {
    if (field || direction) {
      const sorted = getDataSourceData.sortBy(field);
      if (direction === 'asc') {
        sorted.reverse();
      }
      return sorted;
    } else {
      return getDataSourceData;
    }
  },


  actions: {
    sort(column, sortColumn, sortDirection) {
      if (sortColumn === column.get('field')) {
        this.set('currentSort', column);
        this.set('currentSort.direction', sortDirection);
      }
      if ((this.get('currentSort.field') === column.get('field')) && (this.get('currentSort.direction') === 'desc')) {
        this.set('currentSort', column);
        this.set('currentSort.direction', 'asc');
        set(column, 'icon', 'arrow-down-8');
      } else {
        if (this.get('currentSort')) {
          set(this.get('currentSort'), 'className', 'sort');
          set(this.get('currentSort'), 'icon', 'arrow-down-8');
        } else {
          set(column, 'className', 'sort');
          set(column, 'icon', 'arrow-down-8');
        }
        this.set('currentSort', column);
        set(column ? this.get('currentSort') : column, 'className', 'rsa-context-panel__context-data-table__panel__sort-icon');
        set(column, 'icon', 'arrow-up-8');
        this.set('currentSort.direction', 'desc');
      }
    }
  }
});
export default connect(stateToComputed)(DataTableComponent);
