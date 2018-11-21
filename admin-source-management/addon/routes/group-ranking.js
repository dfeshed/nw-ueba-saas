import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { run } from '@ember/runloop';
import { initializeGroup } from 'admin-source-management/actions/creators/group-wizard-creators';

export default Route.extend({
  redux: service(),

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
  }
});
