import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { itemType, isNewItem } from 'rsa-list-manager/selectors/list-manager/selectors';
import { viewChanged } from 'rsa-list-manager/actions/creators/creators';
import { LIST_VIEW } from 'rsa-list-manager/constants/list-manager';
import { createItem, updateItem } from 'rsa-list-manager/actions/creators/item-maintenance-creators';
import { removeTwinIdFromPreQueryPillsData } from 'rsa-list-manager/utils/profile-util';
import _ from 'lodash';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.stateLocation),
  isNewItem: isNewItem(state, attrs.stateLocation)
});

const dispatchToActions = {
  viewChanged,
  createItem,
  updateItem
};

const DetailsFooter = Component.extend({

  tagName: 'footer',
  layout,
  classNames: ['details-footer'],
  stateLocation: undefined,
  editedItem: null,
  originalItem: null,
  itemTransform: null, // function
  isItemEditedValid: false,
  invalidReason: undefined,
  itemReset: null,
  didItemChange: false,
  isValidItem: null,

  didReceiveAttrs() {
    let editedItem = _.cloneDeep(this.get('editedItem'));
    // eslint-disable-next-line prefer-const
    let { originalItem, isValidItem } = this.getProperties('originalItem', 'isValidItem');
    if (editedItem) {
      const itemTransform = this.get('itemTransform');

      // if itemTransform is a function and returns an item, replace edited item with transformed item
      if (typeof itemTransform === 'function') {
        editedItem = itemTransform(editedItem) || editedItem;

        // TODO fix this - temporary solution
        // checking if object is profile
        if (originalItem && !originalItem.hasOwnProperty('columns')) {
          originalItem = itemTransform(originalItem) || originalItem;
        }
      }
    }
    this.set('didItemChange', this._didItemChange(originalItem, editedItem));

    // Use default list-manager validation unless custom validation action is provided
    // Custom validation would check for other parameters of the group unique to itemType
    if (typeof isValidItem == 'function') {
      const { isValid, invalidReason } = isValidItem(editedItem);
      this.set('isItemEditedValid', isValid);
      this.set('inValidReason', invalidReason);
    } else {
      const isValid = !!editedItem?.name;
      this.set('isItemEditedValid', isValid);
    }
  },

  _didItemChange(originalItem, editedItem) {

    // TODO temporary way to check if item is profile
    // do not compare twinId in pillsData
    if (originalItem && !originalItem.hasOwnProperty('columns')) {
      const originalCopy = _.cloneDeep(originalItem);
      const editedCopy = _.cloneDeep(editedItem) || {};

      originalCopy.preQueryPillsData = removeTwinIdFromPreQueryPillsData(_.cloneDeep(originalItem.preQueryPillsData));
      editedCopy.preQueryPillsData = editedItem ? removeTwinIdFromPreQueryPillsData(_.cloneDeep(editedItem.preQueryPillsData)) : [];

      return originalItem.isEditable && !_.isEqual(originalCopy, editedCopy);
    }

    // does not matter if creating a new item or viewing items that are not editable
    return originalItem && originalItem.isEditable && !_.isEqual(originalItem, editedItem);
  },

  actions: {
    detailsDone() {
      this.send('viewChanged', LIST_VIEW, this.get('stateLocation'));
    },

    saveItem() {
      const { editedItem, isNewItem, stateLocation, itemTransform } =
        this.getProperties('editedItem', 'isNewItem', 'stateLocation', 'itemTransform');

      if (isNewItem) {
        this.send('createItem', editedItem, stateLocation, itemTransform);
      } else {
        this.send('updateItem', editedItem, stateLocation, itemTransform, this.get('itemUpdate'));
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DetailsFooter);
