import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed, { alias } from 'ember-computed-decorators';
import { setHostDetailsDataTableSortConfig } from 'investigate-hosts/actions/data-creators/details';

const dispatchToActions = {
  setHostDetailsDataTableSortConfig
};

const HostDetailsDataTable = Component.extend({

  tagName: 'box',

  classNames: ['host-detail__datatable'],

  customSort: null,

  items: [],

  @alias('status')
  isDataLoading: true,

  @computed('items', 'totalItems')
  total(items, totalItems) {
    return totalItems ? totalItems : items.length;
  },

  actions: {
    sort(column) {
      column.set('isDescending', !column.isDescending);
      const customSort = this.get('customSort');
      if (customSort) {
        this.customSort(column);
      } else {
        this.send('setHostDetailsDataTableSortConfig', {
          isDescending: column.isDescending,
          field: column.field
        });
      }
    },

    toggleSelectedRow(item, index, e, table) {

      if (this.get('selectRowAction')) {
        table.set('selectedIndex', index);
        this.selectRowAction(item);
      } else {
        table.set('selectedIndex', -1);
      }

    },
    onCloseServiceModal() {
      this.set('showServiceModal', false);
    },
    onCloseEditFileStatus() {
      this.set('showFileStatusModal', false);
    }
  }
});

export default connect(null, dispatchToActions)(HostDetailsDataTable);