import Ember from 'ember';

const {
  Controller
} = Ember;

export default Controller.extend({

  allItemsChecked: false,

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
