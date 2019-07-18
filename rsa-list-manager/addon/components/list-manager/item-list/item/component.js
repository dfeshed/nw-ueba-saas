import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  tagName: 'li',
  classNames: ['rsa-list-item'],
  classNameBindings: ['isSelected'],
  item: null,
  selectedItem: null,

  // Action that should be triggered when an item is clicked
  itemSelection: () => {},

  isExpanded: true,

  @computed('selectedItem', 'item')
  isSelected(selectedItem, item) {
    return selectedItem && item ? selectedItem.id == item.id : false;
  },

  @computed('item')
  iconName(item) {
    if (item.ootb) {
      return 'lock-close-1';
    }
    return 'settings-1';
  },

  actions: {

    clickAction() {
      if (!this.get('isSelected')) {
        this.get('itemSelection')(this.get('item'));
      }
      this.toggleProperty('isExpanded');
    }

  }
});
