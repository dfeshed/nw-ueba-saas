import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getData, getSortedData } from 'context/util/context-data-modifier';
import { set } from '@ember/object';

const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  dataSources: context.dataSources
});

const DataTableComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__context-data-table',

  @computed('contextData', 'lookupData.[]', 'dSDetails')
  getDataSourceData: (contextData, [lookupData], dSDetails) => contextData ? contextData.resultList : getData(lookupData, dSDetails),

  @computed('getDataSourceData', 'currentSort.icon', 'currentSort.field')
  getDataSourceSortedData: (getDataSourceData, icon, field) => getSortedData(getDataSourceData, icon, field),

  actions: {
    sort(column) {
      if (!this.get('currentSort')) {
        this.set('currentSort', column);
      }
      if (this.get('currentSort.field') !== column.get('field')) {
        set(this.get('currentSort'), 'className', 'sort');
      }
      if (column.icon === 'arrow-down-8') {
        set(column, 'icon', 'arrow-up-8');
        set(column, 'className', 'rsa-context-panel__context-data-table__panel__sort-icon');
      } else {
        set(column, 'icon', 'arrow-down-8');
        set(column, 'className', 'rsa-context-panel__context-data-table__panel__sort-icon');
      }
      this.set('currentSort', column);
    }
  }
});
export default connect(stateToComputed)(DataTableComponent);
