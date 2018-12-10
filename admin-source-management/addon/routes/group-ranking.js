import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { run } from '@ember/runloop';
import { initializeGroup } from 'admin-source-management/actions/creators/group-wizard-creators';

export default Route.extend({
  redux: service(),
  contextualHelp: service(),

  model({ groupId }) {
    run.next(() => {
      this.get('redux').dispatch(initializeGroup(groupId));
    });
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
