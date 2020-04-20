import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'admin-source-management/utils/flash-messages';
import { inject as service } from '@ember/service';
import { computed } from '@ember/object';

import {
  hasSelectedApplyPoliciesItems,
  hasSelectedEditItem,
  selectedEditItem,
  hasSelectedDeleteItems,
  selectedDeleteItems,
  hasSelectedPublishItems,
  selectedPublishItems
} from 'admin-source-management/reducers/usm/groups-selectors';

import {
  deleteGroups,
  publishGroups
} from 'admin-source-management/actions/creators/groups-creators';

const stateToComputed = (state) => ({
  hasSelectedApplyPoliciesItems: hasSelectedApplyPoliciesItems(state),
  hasSelectedEditItem: hasSelectedEditItem(state),
  selectedEditItem: selectedEditItem(state),
  hasSelectedDeleteItems: hasSelectedDeleteItems(state),
  selectedDeleteItems: selectedDeleteItems(state),
  hasSelectedPublishItems: hasSelectedPublishItems(state),
  selectedPublishItems: selectedPublishItems(state)
});

const dispatchToActions = {
  deleteGroups,
  publishGroups
};

const UsmGroupsToolbar = Component.extend({
  classNames: ['usm-groups-toolbar'],
  accessControl: service(),

  cannotEditGroups: computed(
    'hasSelectedEditItem',
    'accessControl.canManageSourceServerGroups',
    function() {
      return !this.hasSelectedEditItem || !this.accessControl?.canManageSourceServerGroups;
    }
  ),

  cannotDeleteGroups: computed(
    'hasSelectedDeleteItems',
    'accessControl.canManageSourceServerGroups',
    function() {
      return !this.hasSelectedDeleteItems || !this.accessControl?.canManageSourceServerGroups;
    }
  ),

  cannotPublishGroups: computed(
    'hasSelectedPublishItems',
    'accessControl.canManageSourceServerGroups',
    function() {
      return !this.hasSelectedPublishItems || !this.accessControl?.canManageSourceServerGroups;
    }
  ),

  actions: {

    handleDeleteGroups() {
      const callbackOptions = {
        onSuccess: () => {
          success('adminUsm.groups.modals.deleteGroups.success');
        },
        onFailure: () => {
          failure('adminUsm.groups.modals.deleteGroups.failure');
        }
      };
      this.send('deleteGroups', callbackOptions);
    },

    handlePublishGroups() {
      const callbackOptions = {
        onSuccess: () => {
          success('adminUsm.groups.modals.publishGroups.success');
        },
        onFailure: () => {
          failure('adminUsm.groups.modals.publishGroups.failure');
        }
      };
      this.send('publishGroups', callbackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupsToolbar);
