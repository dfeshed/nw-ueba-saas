import Component from 'ember-component';
import { connect } from 'ember-redux';
import { alias } from 'ember-computed-decorators';
import { setHostDetailsDataTableSortConfig } from 'investigate-hosts/actions/data-creators/details';

const dispatchToActions = {
  setHostDetailsDataTableSortConfig
};

const HostDetailsDataTable = Component.extend({

  tagName: 'box',

  classNames: ['host-detail__datatable'],

  columnConfig: null,

  @alias('status')
  isDataLoading: true,

  didRender() {
    // Increase the rsa-data-table-body-rows border dynamically
    const rsaDataTableBody = this.$('.rsa-data-table-body');
    const dataTableTotalWidth = rsaDataTableBody[0] ? rsaDataTableBody[0].scrollWidth : 0;

    if (dataTableTotalWidth) {
      this.$('.rsa-data-table-body-rows').innerWidth(dataTableTotalWidth);
      this.$('.rsa-data-table-header').innerWidth(dataTableTotalWidth);
    }
  },

  actions: {
    sort(column) {
      if (column.isDescending !== undefined && column.isDescending === false) {
        column.set('isDescending', true);
      } else {
        column.set('isDescending', false);
      }
      this.send('setHostDetailsDataTableSortConfig', {
        isDescending: column.isDescending,
        field: column.field
      });
    },

    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
      this.sendAction('selectRowAction', item);
    }
  }
});

export default connect(null, dispatchToActions)(HostDetailsDataTable);
