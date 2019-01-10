import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { bootstrapInvestigateHosts } from 'investigate-hosts/actions/data-creators/host';

export default Route.extend({
  redux: service(),

  contextualHelp: service(),

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invHosts'));
  },

  model({ query }) {
    const redux = this.get('redux');
    return redux.dispatch(bootstrapInvestigateHosts(query));
  },

  resetController(controller, isExiting) {
    if (isExiting) {
      const queryParams = controller.get('queryParams');
      for (let i = 0; i < queryParams.length; i++) {
        controller.set(queryParams[i], null);
      }
    }
  },

  deactivate() {
    this.set('contextualHelp.topic', null);
  }
});
