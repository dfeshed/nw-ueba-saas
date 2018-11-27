import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'admin-source-management/utils/flash-messages';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

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

  @computed('hasSelectedEditItem', 'accessControl.canManageSourceServerGroups')
  cannotEditGroups(hasSelectedEditItem, canManageSourceServerGroups) {
    return !hasSelectedEditItem || !canManageSourceServerGroups;
  },

  @computed('hasSelectedDeleteItems', 'accessControl.canManageSourceServerGroups')
  cannotDeleteGroups(hasSelectedDeleteItems, canManageSourceServerGroups) {
    return !hasSelectedDeleteItems || !canManageSourceServerGroups;
  },

  @computed('hasSelectedPublishItems', 'accessControl.canManageSourceServerGroups')
  cannotPublishGroups(hasSelectedPublishItems, canManageSourceServerGroups) {
    return !hasSelectedPublishItems || !canManageSourceServerGroups;
  },

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
