import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { getData, getSortedData } from 'context/utils/context-data-modifier';
import { set, computed } from '@ember/object';

const stateToComputed = ({ context: { context } }) => ({
  lookupData: context.lookupData
});

const DataTableComponent = Component.extend({
  layout,
  tagName: 'div',
  currentSort: null,
  classNames: ['rsa-context-panel__context-data-table', 'rsa-context-panel__context-data-table__panel'],

  dataSourceData: computed('contextData', 'lookupData.[]', 'dataSourceDetails', function() {
    const [lookupData] = this.lookupData;
    return this.contextData ? this.contextData.resultList : getData(lookupData, this.dataSourceDetails);
  }),

  columnsData: computed('dataSourceDetails', function() {
    return this.dataSourceDetails.columns && this.dataSourceDetails.columns.asMutable ? this.dataSourceDetails.columns.asMutable() : this.dataSourceDetails.columns;
  }),

  dataSourceSortedData: computed('dataSourceData', 'currentSort.icon', 'currentSort.field', function() {
    return getSortedData(this.dataSourceData, this.currentSort?.icon, this.currentSort?.field);
  }),

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
