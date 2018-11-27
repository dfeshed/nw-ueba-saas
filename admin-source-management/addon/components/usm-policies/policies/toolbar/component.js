import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'admin-source-management/utils/flash-messages';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

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

  @computed('hasSelectedEditItem', 'accessControl.canManageSourceServerPolicies')
  cannotEditPolicies(hasSelectedEditItem, canManageSourceServerPolicies) {
    return !hasSelectedEditItem || !canManageSourceServerPolicies;
  },

  @computed('hasSelectedDeleteItems', 'accessControl.canManageSourceServerPolicies')
  cannotDeletePolicies(hasSelectedDeleteItems, canManageSourceServerPolicies) {
    return !hasSelectedDeleteItems || !canManageSourceServerPolicies;
  },

  @computed('hasSelectedPublishItems', 'accessControl.canManageSourceServerPolicies')
  cannotPublishPolicies(hasSelectedPublishItems, canManageSourceServerPolicies) {
    return !hasSelectedPublishItems || !canManageSourceServerPolicies;
  },

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
