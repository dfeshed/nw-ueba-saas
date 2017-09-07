import Component from 'ember-component';
import computed from 'ember-computed-decorators';

import layout from './template';

export default Component.extend({
  layout,

  tagName: 'button',

  classNameBindings: ['columnClass'],

  sortField: null,

  column: null,

  sortBy: null,

  isSortDescending: false,

  @computed('sortField', 'column.field', 'column.sortField')
  columnClass(sortField, field, columnSortField) {
    let columnClass = 'column-sort hideSort expand';
    if (sortField === field || sortField === columnSortField) {
      columnClass = 'column-sort expand';
    }
    return columnClass;
  },

  @computed('isSortDescending')
  iconName(isSortDescending) {
    return isSortDescending ? 'arrow-down-12' : 'arrow-up-12';
  },

  click() {
    const { column, isSortDescending } = this.getProperties('column', 'isSortDescending');
    const field = column.sortField || column.field;
    const sortDirection = !isSortDescending;
    this.sortBy(field, sortDirection);
  }
});
