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

  // provided by consumer to force disabled
  disabledOverride: undefined,

  // message used for tooltip when disabled is forced
  disabledOverrideMessage: undefined,

  @computed('isEditable', 'isNewItem')
  showDeleteIcon(isEditable, isNewItem) {
    return isEditable && !isNewItem;
  },

  @computed('isSelected', 'disabledOverride', 'disabledOverrideMessage')
  disabledDetails(isSelected, disabledOverride = false, disabledOverrideMessage = '') {

    // if is disabled by caller, provide that callers message
    if (disabledOverride === true) {
      return {
        disabled: true,
        message: disabledOverrideMessage
      };
    }

    // if is disabled because it is selected, provide message
    if (isSelected) {
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
  },

  actions: {
    delete() {
      this.send('deleteItem', this.get('stateLocation'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DeleteIcon);
