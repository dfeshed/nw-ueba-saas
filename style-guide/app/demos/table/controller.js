import Controller from '@ember/controller';

export default Controller.extend({
  allItemsChecked: false,
  noResultsMessage: 'No events found. Your filter criteria did not match any records.',
  // Table of contents entries
  toc: [
    {
      selector: '#basic-table',
      title: 'Basic Table'
    },
    {
      selector: '#no-results',
      title: 'No Results'
    },
    {
      selector: '#row-select',
      title: 'Selecting Rows'
    },
    {
      selector: '#column-reorder-resize',
      title: 'Reordering/Resizing Columns'
    },
    {
      selector: '#error-handling',
      title: 'Error Handling'
    },
    {
      selector: '#sorting',
      title: 'Sorting'
    }
  ],

  actions: {
    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
    },
    toggleItemSelection(item) {
      item.toggleProperty('checked');
    },

    toggleItemsSelection(items) {
      if (this.get('allItemsChecked')) {
        items.setEach('checked', false);
      } else {
        items.setEach('checked', true);
      }

      this.toggleProperty('allItemsChecked');
    },

    sort(column) {
      if ((this.get('currentSort.field') === column.get('field')) && (this.get('currentSort.direction') === 'desc')) {
        this.set('currentSort.direction', 'asc');
      } else {
        this.set('currentSort', column);
        this.set('currentSort.direction', 'desc');
      }

      const sorted = this.get('model.sortableItems').sortBy(this.get('currentSort.field'));
      if (this.get('currentSort.direction') === 'asc') {
        sorted.reverse();
      }
      this.set('model.sortableItems', sorted);

    }
  }
});
