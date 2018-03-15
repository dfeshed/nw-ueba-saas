import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getData, getSortedData } from 'context/util/context-data-modifier';
import { set } from '@ember/object';

const stateToComputed = ({ context: { context } }) => ({
  lookupData: context.lookupData
});

const DataTableComponent = Component.extend({
  layout,
  tagName: 'div',
  currentSort: null,
  classNames: ['rsa-context-panel__context-data-table', 'rsa-context-panel__context-data-table__panel'],

  @computed('contextData', 'lookupData.[]', 'dataSourceDetails')
  dataSourceData: (contextData, [lookupData], dataSourceDetails) => contextData ? contextData.resultList : getData(lookupData, dataSourceDetails),

  @computed('dataSourceDetails')
  columnsData: ({ columns }) => columns && columns.asMutable ? columns.asMutable() : columns,

  @computed('dataSourceData', 'currentSort.icon', 'currentSort.field')
  dataSourceSortedData: (dataSourceData, icon, field) => getSortedData(dataSourceData, icon, field),

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
