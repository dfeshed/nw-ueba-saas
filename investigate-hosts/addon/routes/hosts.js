import { lookup } from 'ember-dependency-lookup';
import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { bootstrapInvestigateHosts, initializeHostDetailsPage } from 'investigate-hosts/actions/data-creators/host';
import { userLeftListPage, resetDetailsInputAndContent } from 'investigate-hosts/actions/ui-state-creators';

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
    tabName: {
      refreshModel: true
    },
    subTabName: {
      refreshModel: true
    },
    pid: {
      refreshModel: true
    },
    /**
     * selected serviceId for multi-server endpoint server
     * @type {string}
     * @public
     */
    sid: {
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
    const redux = this.get('redux');
    const { sid, machineId, query, tabName } = params;
    // if both sid and machine id there then that is host details page
    if (sid && machineId) {
      this.set('contextualHelp.topic', this.get(HELP_ID_MAPPING[tabName]));
      redux.dispatch(initializeHostDetailsPage(params));
    } else {
      this.set('contextualHelp.topic', this.get('contextualHelp.invHosts'));
      redux.dispatch(resetDetailsInputAndContent());
      if (!this.get('listLoaded')) {
        this.set('listLoaded', true);
        return redux.dispatch(bootstrapInvestigateHosts(query));
      }
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

  // On deactivating the route send the user left page action to cleanup the state if any
  deactivate() {
    const redux = this.get('redux');
    const request = lookup('service:request');
    redux.dispatch(userLeftListPage());
    redux.dispatch(resetDetailsInputAndContent()); // Clear the details input
    request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
    this.set('contextualHelp.topic', null);
    this.set('listLoaded', false);
  }
});
