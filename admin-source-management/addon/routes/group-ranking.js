import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeGroup } from 'admin-source-management/actions/creators/group-wizard-creators';
import {
  fetchEndpointServers,
  fetchLogServers,
  fetchFileSourceTypes
} from 'admin-source-management/actions/creators/policy-wizard-creators';

export default Route.extend({
  redux: service(),
  contextualHelp: service(),

  model() {
    const redux = this.get('redux');
    // fetch policy lookup data
    redux.dispatch(fetchEndpointServers());
    redux.dispatch(fetchLogServers());
    redux.dispatch(fetchFileSourceTypes());
    // trigger fetching group lookup data without trying to fetch a group
    const groupId = 'create-new';
    redux.dispatch(initializeGroup(groupId));
    return {
      groupId
    };
  },

  actions: {
    transitionToGroups() {
      this.transitionTo('groups');
    }
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.usmModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.usmGroupsWizardRanking'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  }
});
