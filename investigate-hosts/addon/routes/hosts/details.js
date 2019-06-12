import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeHostDetailsPage } from 'investigate-hosts/actions/data-creators/host';
import { resetDetailsInputAndContent } from 'investigate-hosts/actions/ui-state-creators';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { getOwner } from '@ember/application';

const HELP_ID_MAPPING = {
  'OVERVIEW': 'contextualHelp.invHostsOverview',
  'PROCESS': 'contextualHelp.invHostsProcess',
  'ANOMALIES': 'contextualHelp.invHostsAnomalies',
  'AUTORUNS': 'contextualHelp.invHostsAutoruns',
  'FILES': 'contextualHelp.invHostsFiles',
  'DRIVERS': 'contextualHelp.invHostsDrivers',
  'LIBRARIES': 'contextualHelp.invHostsLibraries',
  'SYSTEM': 'contextualHelp.invHostsSysInfo',
  'DOWNLOADS': 'contextualHelp.invHostsDownloads'
};

export default Route.extend({

  i18n: service(),

  redux: service(),

  accessControl: service(),

  contextualHelp: service(),

  listLoaded: false,

  isPageLoading: true,

  queryParams: {
    machineId: {
      refreshModel: true
    },
    filterId: {
      refreshModel: true
    },
    subTabName: {
      refreshModel: true
    },
    pid: {
      refreshModel: true
    },
    sid: {
      refreshModel: true
    },
    tabName: {
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
    const { sid, machineId, tabName } = params;
    if (sid && machineId) {
      this.set('contextualHelp.topic', this.get(HELP_ID_MAPPING[tabName]));
      return redux.dispatch(initializeHostDetailsPage(params, isPageReload));
    }
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
  }
});
