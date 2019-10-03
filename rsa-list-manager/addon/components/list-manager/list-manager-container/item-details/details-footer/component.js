import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { itemType, isNewItem } from 'rsa-list-manager/selectors/list-manager/selectors';
import { viewChanged } from 'rsa-list-manager/actions/creators/creators';
import { LIST_VIEW } from 'rsa-list-manager/constants/list-manager';
import { createItem } from 'rsa-list-manager/actions/creators/item-maintenance-creators';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.stateLocation),
  isNewItem: isNewItem(state, attrs.stateLocation)
});

const dispatchToActions = {
  viewChanged,
  createItem
};

const DetailsFooter = Component.extend({

  tagName: 'footer',
  layout,
  classNames: ['details-footer'],
  stateLocation: undefined,
  item: null,

  // valid edited item sent by user
  editedItem: null,

  actions: {
    detailsDone() {
      this.send('viewChanged', LIST_VIEW, this.get('stateLocation'));
    },

    saveItem() {
      const { editedItem, isNewItem, stateLocation, itemTransform } = this.getProperties('editedItem', 'isNewItem', 'stateLocation', 'itemTransform');

      if (isNewItem) {
        this.send('createItem', editedItem, stateLocation, itemTransform);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DetailsFooter);
