import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
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

  // provided by consumer to force disabled
  disabledOverride: undefined,

  // message used for tooltip when disabled is forced
  disabledOverrideMessage: undefined,

  showDeleteIcon: computed('isEditable', 'isNewItem', function() {
    return this.isEditable && !this.isNewItem;
  }),

  disabledDetails: computed('isSelected', 'disabledOverride', function() {

    // if is disabled by caller, provide that callers message
    if (this.disabledOverride?.disableDelete === true) {
      return {
        disabled: true,
        message: this.disabledOverride.reason
      };
    }

    // if is disabled because it is selected, provide message
    if (this.isSelected) {
      return {
        disabled: true,
        message: this.i18n.t('rsaListManager.iconMessage.disabled.delete')
      };
    }

    // otherwise not disabled
    return {
      disabled: false,
      message: ''
    };
  }),

  actions: {
    delete() {
      this.send('deleteItem', this.get('stateLocation'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DeleteIcon);
