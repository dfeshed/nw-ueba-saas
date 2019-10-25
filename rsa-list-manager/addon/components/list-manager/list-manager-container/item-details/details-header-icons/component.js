import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import {
  helpId,
  hasContextualHelp,
  isEditable,
  isNewItem,
  editItemIsSelected
} from 'rsa-list-manager/selectors/list-manager/selectors';
import { deleteItem } from 'rsa-list-manager/actions/creators/item-maintenance-creators';

const stateToComputed = (state, attrs) => ({
  hasContextualHelp: hasContextualHelp(state, attrs.stateLocation),
  helpId: helpId(state, attrs.stateLocation),
  isEditable: isEditable(state, attrs.stateLocation),
  isNewItem: isNewItem(state, attrs.stateLocation),
  isSelected: editItemIsSelected(state, attrs.stateLocation)
});

const dispatchToActions = {
  deleteItem
};

const DetailsHeaderIcons = Component.extend({
  layout,
  classNames: ['details-header-icons'],
  contextualHelp: service(),
  stateLocation: undefined,
  item: undefined,

  @computed('isEditable', 'isNewItem')
  showDeleteIcon(isEditable, isNewItem) {
    return isEditable && !isNewItem;
  },

  @computed('isSelected', 'item')
  isDeletable(isSelected, item = {}) {
    return !isSelected && !!item.isDeletable;
  },

  @computed('isSelected', 'item')
  titleMessage(isSelected, item = {}) {
    if (isSelected) {
      return this.i18n.t('rsaListManager.iconMessage.disabled.delete');
    } else if (!item.isDeletable && item.undeletableReason) {
      return item.undeletableReason;
    } else {
      return '';
    }
  },

  actions: {
    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    },

    delete() {
      this.send('deleteItem', this.get('stateLocation'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DetailsHeaderIcons);
