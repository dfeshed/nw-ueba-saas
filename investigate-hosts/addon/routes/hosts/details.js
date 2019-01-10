import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeHostDetailsPage } from 'investigate-hosts/actions/data-creators/host';

const HELP_ID_MAPPING = {
  'OVERVIEW': 'contextualHelp.invHostsOverview',
  'PROCESS': 'contextualHelp.invHostsProcess',
  'ANOMALIES': 'contextualHelp.invHostsAnomalies',
  'AUTORUNS': 'contextualHelp.invHostsAutoruns',
  'FILES': 'contextualHelp.invHostsFiles',
  'DRIVERS': 'contextualHelp.invHostsDrivers',
  'LIBRARIES': 'contextualHelp.invHostsLibraries',
  'SYSTEM': 'contextualHelp.invHostsSysInfo'
};

export default Route.extend({

  i18n: service(),

  redux: service(),

  accessControl: service(),

  contextualHelp: service(),

  listLoaded: false,

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
    }
  },


  model(params) {
    const redux = this.get('redux');
    const { sid, machineId, tabName } = params;
    if (sid && machineId) {
      this.set('contextualHelp.topic', this.get(HELP_ID_MAPPING[tabName]));
      return redux.dispatch(initializeHostDetailsPage(params));
    }
  },

  resetController(controller, isExiting) {
    if (isExiting) {
      const queryParams = controller.get('queryParams');
      for (let i = 0; i < queryParams.length; i++) {
        controller.set(queryParams[i], null);
      }
    }
  }
});
