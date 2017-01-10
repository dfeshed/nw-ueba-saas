import Ember from 'ember';

const {
  Controller
} = Ember;

export default Controller.extend({

  allItemsChecked: false,

  actions: {
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
