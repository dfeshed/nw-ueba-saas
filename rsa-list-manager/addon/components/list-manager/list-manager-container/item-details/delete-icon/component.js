import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import {
  isEditable,
  isNewItem,
  editItemIsSelected
} from 'rsa-list-manager/selectors/list-manager/selectors';
import { deleteItem } from 'rsa-list-manager/actions/creators/item-maintenance-creators';

const stateToComputed = (state, attrs) => ({
  isEditable: isEditable(state, attrs.stateLocation),
  isNewItem: isNewItem(state, attrs.stateLocation),
  isSelected: editItemIsSelected(state, attrs.stateLocation)
});

const dispatchToActions = {
  deleteItem
};

const DeleteIcon = Component.extend({
  tagName: '',
  layout,
  stateLocation: undefined,

  @computed('isEditable', 'isNewItem')
  showDeleteIcon(isEditable, isNewItem) {
    return isEditable && !isNewItem;
  },

  actions: {
    delete() {
      this.send('deleteItem', this.get('stateLocation'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DeleteIcon);
