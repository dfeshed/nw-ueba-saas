import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'admin-source-management/utils/flash-messages';
import { inject as service } from '@ember/service';
import { computed } from '@ember/object';

import {
  hasSelectedEditItem,
  selectedEditItem,
  hasSelectedDeleteItems,
  selectedDeleteItems,
  hasSelectedPublishItems,
  selectedPublishItems
} from 'admin-source-management/reducers/usm/policies-selectors';

import {
  deletePolicies,
  publishPolicies
} from 'admin-source-management/actions/creators/policies-creators';

const stateToComputed = (state) => ({
  hasSelectedEditItem: hasSelectedEditItem(state),
  selectedEditItem: selectedEditItem(state),
  hasSelectedDeleteItems: hasSelectedDeleteItems(state),
  selectedDeleteItems: selectedDeleteItems(state),
  hasSelectedPublishItems: hasSelectedPublishItems(state),
  selectedPublishItems: selectedPublishItems(state)
});

const dispatchToActions = {
  deletePolicies,
  publishPolicies
};

const UsmPoliciesToolbar = Component.extend({
  classNames: ['usm-policies-toolbar'],
  accessControl: service(),

  cannotEditPolicies: computed(
    'hasSelectedEditItem',
    'accessControl.canManageSourceServerPolicies',
    function() {
      return !this.hasSelectedEditItem || !this.accessControl?.canManageSourceServerPolicies;
    }
  ),

  cannotDeletePolicies: computed(
    'hasSelectedDeleteItems',
    'accessControl.canManageSourceServerPolicies',
    function() {
      return !this.hasSelectedDeleteItems || !this.accessControl?.canManageSourceServerPolicies;
    }
  ),

  cannotPublishPolicies: computed(
    'hasSelectedPublishItems',
    'accessControl.canManageSourceServerPolicies',
    function() {
      return !this.hasSelectedPublishItems || !this.accessControl?.canManageSourceServerPolicies;
    }
  ),

  actions: {

    handleDeletePolicies() {
      const callbackOptions = {
        onSuccess: () => {
          success('adminUsm.policies.modals.deletePolicies.success');
        },
        onFailure: () => {
          failure('adminUsm.policies.modals.deletePolicies.failure');
        }
      };
      this.send('deletePolicies', callbackOptions);
    },

    handlePublishPolicies() {
      const callbackOptions = {
        onSuccess: () => {
          success('adminUsm.policies.modals.publishPolicies.success');
        },
        onFailure: () => {
          failure('adminUsm.policies.modals.publishPolicies.failure');
        }
      };
      this.send('publishPolicies', callbackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesToolbar);
