import Ember from 'ember';

const {
  Controller
} = Ember;

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
    }
  }
});
