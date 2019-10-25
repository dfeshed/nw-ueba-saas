import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { itemType, editItem, isItemsLoading } from 'rsa-list-manager/selectors/list-manager/selectors';
import { beginEditItem } from 'rsa-list-manager/actions/creators/creators';
import _ from 'lodash';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.stateLocation),
  // TODO edit followup PR fix cloning when item is between start updating and success
  originalItem: _.cloneDeep(editItem(state, attrs.stateLocation)),
  isItemsLoading: isItemsLoading(state, attrs.stateLocation)
});

const dispatchToActions = {
  beginEditItem
};

const ItemDetails = Component.extend({
  layout,
  classNames: ['item-details'],
  stateLocation: undefined,
  editedItem: null,

  @computed('originalItem', 'itemType')
  heading(originalItem, itemType) {
    if (originalItem) {
      return `${itemType} Details`;
    } else {
      return `Create ${itemType}`;
    }
  },

  actions: {
    itemEdited(editedItem) {
      // the format of the edited item at this point can be different from the original item
      // eg. original column group has transformed columns { field, title },
      // edited column group has actual columns columns { metaName, displayName }
      this.set('editedItem', editedItem);
    },

    resetItem() {
      this.send('beginEditItem', this.get('originalItem').id, this.get('stateLocation'));
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ItemDetails);
