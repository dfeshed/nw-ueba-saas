import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeHostPage, getAllSchemas } from 'investigate-hosts/actions/data-creators/host';
import { userLeftListPage } from 'investigate-hosts/actions/ui-state-creators';
import { ping } from 'streaming-data/services/data-access/requests';
import run from 'ember-runloop';

const HELP_ID_MAPPING = {
  'OVERVIEW': 'contextualHelp.invHostsOverview',
  'PROCESS': 'contextualHelp.invHostsProcess',
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

  queryParams: {
    machineId: {
      refreshModel: true
    },
    filterId: {
      refreshModel: true
    },
    tabName: {
      refreshModel: true
    }
  },

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('investigateHosts.title') });
  },

  beforeModel() {
    if (!this.get('accessControl.hasInvestigateHostsAccess')) {
      this.transitionTo('permission-denied');
    }
  },

  model(params) {
    return ping('endpoint-server-ping')
      .then(() => {
        const redux = this.get('redux');
        const { machineId, tabName = 'OVERVIEW' } = params;
        if (machineId) {
          this.set('contextualHelp.topic', this.get(HELP_ID_MAPPING[tabName]));
        } else {
          this.set('contextualHelp.topic', this.get('contextualHelp.invHosts'));
        }
        run.next(() => {
          redux.dispatch(initializeHostPage(params));
        });
      })
      .catch(function() {
        return { endpointServerOffline: true };
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

  // On activating the route get the all the schema, schema response is used to build the column configuration and filter
  activate() {
    this.get('redux').dispatch(getAllSchemas());
  },

  // On deactivating the route send the user left page action to cleanup the state if any
  deactivate() {
    this.get('redux').dispatch(userLeftListPage());
    this.set('contextualHelp.topic', null);
  }
});
