import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'admin-source-management/utils/flash-messages';

import {
  hasSelectedApplyPoliciesItems,
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
