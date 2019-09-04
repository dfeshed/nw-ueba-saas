import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import sort from 'fast-sort';

export default Component.extend({
  classNames: ['column-group-details'],
  columnGroup: null,

  @computed('columnGroup')
  sortedColumns(columnGroup) {
    if (columnGroup && columnGroup.columns) {
      const columns = columnGroup.columns.asMutable ? columnGroup.columns.asMutable() : columnGroup.columns;
      sort(columns).by([{ asc: (column) => column.field.toUpperCase() }]);
      return columns;
    }
    return null;
  }
});
