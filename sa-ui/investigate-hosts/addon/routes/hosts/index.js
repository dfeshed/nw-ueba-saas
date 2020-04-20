import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { bootstrapInvestigateHosts } from 'investigate-hosts/actions/data-creators/host';
import { next } from '@ember/runloop';
import { userLeftListPage } from 'investigate-hosts/actions/ui-state-creators';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { getOwner } from '@ember/application';

export default Route.extend({
  redux: service(),

  contextualHelp: service(),

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invHosts'));
    // On back button (from other pages) => services are not loaded and lookup failed.
    // Forcefully initializing the container solves the problem
    initialize(getOwner(this));
  },

  model({ query }) {
    next(() => {
      const redux = this.get('redux');
      redux.dispatch(bootstrapInvestigateHosts(query));
    });
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
    const redux = this.get('redux');
    this.set('contextualHelp.topic', null);
    redux.dispatch(userLeftListPage());
  }
});
