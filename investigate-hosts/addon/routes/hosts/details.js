import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeHostDetailsPage } from 'investigate-hosts/actions/data-creators/host-details';
import { resetDetailsInputAndContent } from 'investigate-hosts/actions/ui-state-creators';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { getOwner } from '@ember/application';


export default Route.extend({

  i18n: service(),

  redux: service(),

  accessControl: service(),

  listLoaded: false,

  isPageLoading: true,

  queryParams: {
    sid: {
      refreshModel: true
    }
  },

  activate() {
    this.set('isPageLoading', false);
  },

  beforeModel() {
    // On back button (from Event Analysis) => services are not loaded and lookup failed.
    // Forcefully initializing the container solves the problem
    initialize(getOwner(this));
  },

  model(params) {
    const redux = this.get('redux');
    const isPageReload = this.get('isPageLoading');
    const { sid, id } = params;
    if (sid && id) {
      return redux.dispatch(initializeHostDetailsPage(params, isPageReload)).then(() => {
        return params;
      }).catch(() => {
        return {};
      });
    }
    return params;
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
    this.set('isPageLoading', true); // Reset the flag
    redux.dispatch(resetDetailsInputAndContent()); // Clear the details input
  },

  actions: {
    navigateToTab(params) {
      if (params) {
        const { subTabName, tabName, scanTime, checksum } = params;
        this.transitionTo('hosts.details.tab', tabName, { queryParams: { subTabName, scanTime, checksum } });
      } else {
        this.transitionTo({ queryParams: { subTabName: null, scanTime: null, checksum: null } });
      }
    }
  }
});
