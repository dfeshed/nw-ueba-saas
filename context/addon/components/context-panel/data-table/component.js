import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import { needToDisplay, getData } from 'context/util/context-data-modifier';

const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  dataSources: context.dataSources
});

const DataTableComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__context-data-table',

  @computed('contextData', 'lookupData.[]', 'dSDetails')
  getDataSourceData: (contextData, [lookupData], dSDetails) => contextData ? contextData.data : getData(lookupData, dSDetails),

  @computed('contextData', 'lookupData.[]', 'dSDetails', 'dataSources')
  displayTable: (contextData, [lookupData], dSDetails, dataSources) => needToDisplay(contextData, lookupData, dSDetails, dataSources),

  actions: {
    sort(column) {
      if ((this.get('currentSort.field') === column.get('field')) && (this.get('currentSort.direction') === 'desc')) {
        this.set('currentSort.direction', 'asc');
      } else {
        this.set('currentSort', column);
        this.set('currentSort.direction', 'desc');
      }

      const sorted = this.get('getDataSourceData').sortBy(this.get('currentSort.field'));
      if (this.get('currentSort.direction') === 'asc') {
        sorted.reverse();
      }
      this.set('getDataSourceData', sorted);
    }
  }
});
export default connect(stateToComputed)(DataTableComponent);
