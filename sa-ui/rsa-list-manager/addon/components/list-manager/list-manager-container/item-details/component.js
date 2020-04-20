import { computed } from '@ember/object';
import Component from '@ember/component';
import _ from 'lodash';
import { run } from '@ember/runloop';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

import layout from './template';
import {
  itemType,
  editItem,
  isItemsLoading,
  helpId,
  hasContextualHelp
} from 'rsa-list-manager/selectors/list-manager/selectors';
import { beginEditItem } from 'rsa-list-manager/actions/creators/creators';

const dispatchToActions = {
  beginEditItem
};

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
  originalItem: computed('item', 'didReset', function() {
    return this.didReset ? _.cloneDeep(this.item) : this.item;
  }),

  heading: computed('originalItem', 'itemType', function() {
    if (this.originalItem) {
      return `${this.itemType} Details`;
    } else {
      return `Create ${this.itemType}`;
    }
  }),

  actions: {
    itemEdited(editedItem) {
      // the format of the edited item at this point can be different from the original item
      // eg. original column group has transformed columns { field, title },
      // edited column group has actual columns columns { metaName, displayName }
      this.set('editedItem', editedItem);
    },

    resetItem() {
      this.set('didReset', true);

      // The consumer of list-mananger will have to be notified about reset
      this.send('beginEditItem', this.get('originalItem').id, this.get('stateLocation'));

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

export default connect(stateToComputed, dispatchToActions)(ItemDetails);
