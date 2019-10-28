import Component from '@ember/component';
import _ from 'lodash';
import { run } from '@ember/runloop';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

import layout from './template';
import {
  itemType,
  editItem,
  isItemsLoading,
  helpId,
  hasContextualHelp
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  hasContextualHelp: hasContextualHelp(state, attrs.stateLocation),
  helpId: helpId(state, attrs.stateLocation),
  itemType: itemType(state, attrs.stateLocation),
  item: editItem(state, attrs.stateLocation),
  isItemsLoading: isItemsLoading(state, attrs.stateLocation)
});

const ItemDetails = Component.extend({
  layout,
  classNames: ['item-details'],
  contextualHelp: service(),
  stateLocation: undefined,
  editedItem: null,
  didReset: false,

  /*
   * original item needs to be recomputed for re-render, every time
   * 1. item is updated, or
   * 2. form is reset in which case item needs to be force updated via cloning
   */
  @computed('item', 'didReset')
  originalItem(item, didReset) {
    return didReset ? _.cloneDeep(item) : item;
  },

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
      this.set('didReset', true);

      // let rendering happen then set it back
      run.next(() => {
        this.set('didReset', false);
      });
    },

    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }

});

export default connect(stateToComputed)(ItemDetails);
