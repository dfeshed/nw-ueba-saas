import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { run } from '@ember/runloop';
import { initializeGroup } from 'admin-source-management/actions/creators/group-wizard-creators';

export default Route.extend({
  redux: service(),

  model({ group_id }) {
    run.next(() => {
      this.get('redux').dispatch(initializeGroup(group_id));
    });
    return {
      groupId: group_id
    };
  },

  actions: {
    transitionToGroups() {
      this.transitionTo('groups');
    }
  }
});
