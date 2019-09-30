import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { itemType, editItem, isItemsLoading } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.stateLocation),
  item: editItem(state, attrs.stateLocation),
  isItemsLoading: isItemsLoading(state, attrs.stateLocation)
});

const ItemDetails = Component.extend({
  layout,
  classNames: ['item-details'],
  stateLocation: undefined,
  editedItem: null,

  @computed('item', 'itemType')
  heading(item, itemType) {
    // TODO bhanun add to translation
    if (item) {
      return `${itemType} Details`;
    } else {
      return `Create ${itemType}`;
    }
  },

  actions: {
    itemEdited(editedItem) {
      this.set('editedItem', editedItem);
    }
  }

});

export default connect(stateToComputed)(ItemDetails);
