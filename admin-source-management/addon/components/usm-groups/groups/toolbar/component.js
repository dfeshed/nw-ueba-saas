import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isPresent } from '@ember/utils';
import computed, { notEmpty } from 'ember-computed-decorators';
import { success, failure } from 'admin-source-management/utils/flash-messages';

import {
  deleteGroups,
  publishGroups
} from 'admin-source-management/actions/creators/groups-creators';

const stateToComputed = (state) => ({
  _items: state.usm.groups.items,
  _selectedItems: state.usm.groups.itemsSelected
});

const dispatchToActions = {
  deleteGroups,
  publishGroups
};

const UsmGroupsToolbar = Component.extend({
  classNames: ['usm-groups-toolbar'],

  @notEmpty('_selectedItems')
  canDelete: true,

  @notEmpty('_selectedItems')
  canApplyPolicies: true,

  @computed('_selectedItems', '_items')
  canPublish(selectedItems, items) {
    if (isPresent(selectedItems) && isPresent(items)) {
      return (selectedItems.filter((selected) => items.findBy('id', selected).dirty));
    }
  },

  actions: {

    handleDeleteGroups(selectedItems) {
      const callbackOptions = {
        onSuccess: () => {
          success('adminUsm.groups.modals.deleteGroups.success', { numItems: selectedItems.length });
        },
        onFailure: () => {
          failure('adminUsm.groups.modals.deleteGroups.failure');
        }
      };
      this.send('deleteGroups', selectedItems, callbackOptions);
    },

    handlePublishGroups(selectedItems) {
      const callbackOptions = {
        onSuccess: () => {
          success('adminUsm.groups.modals.publishGroups.success', { numItems: selectedItems.length });
        },
        onFailure: () => {
          failure('adminUsm.groups.modals.publishGroups.failure');
        }
      };
      this.send('publishGroups', selectedItems, callbackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupsToolbar);
