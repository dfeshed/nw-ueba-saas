import Component from 'ember-component';
import { alias } from 'ember-computed-decorators';

export default Component.extend({

  tagName: 'box',

  classNames: ['host-detail__datatable'],

  columnConfig: null,

  @alias('items')
  data: null,

  @alias('status')
  isDataLoading: true,

  didRender() {
    // Increase the rsa-data-table-body-rows border dynamically
    const rsaDataTableBody = this.$('.rsa-data-table-body');
    const dataTableTotalWidth = rsaDataTableBody[0] ? rsaDataTableBody[0].scrollWidth : '0';
    this.$('.rsa-data-table-body-rows').innerWidth(dataTableTotalWidth);
  },

  actions: {
    sort(column) {
      if (column.isDescending !== undefined && column.isDescending === false) {
        column.set('isDescending', true);
      } else {
        column.set('isDescending', false);
      }
      const items = this.get('data');
      const newList = items.sortBy(column.get('field'));
      if (column.isDescending) {
        newList.reverse();
      }
      this.set('items', newList);

    },
    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
      this.sendAction('selectRowAction', item);
    }
  }
});
