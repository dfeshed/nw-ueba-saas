import { lookup } from 'ember-dependency-lookup';
import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeHostPage, getAllSchemas } from 'investigate-hosts/actions/data-creators/host';
import { userLeftListPage, resetDetailsInputAndContent } from 'investigate-hosts/actions/ui-state-creators';
import { run } from '@ember/runloop';
import { toggleProcessDetailsView } from 'investigate-hosts/actions/data-creators/process';
import { getRestrictedFileList } from 'investigate-shared/actions/data-creators/file-status-creators';
import {
  getEndpointServers,
  isEndpointServerOffline,
  setSelectedEndpointServer } from 'investigate-shared/actions/data-creators/endpoint-server-creators';

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

  activate() {
    const redux = this.get('redux');
    // get host list
    redux.dispatch(getEndpointServers(getAllSchemas));
    redux.dispatch(getRestrictedFileList('MACHINE'));
  },

  model(params) {
    const redux = this.get('redux');
    const { sid, machineId } = params;
    const request = lookup('service:request');
    const selectedServerId = redux.getState().endpointQuery.serverId;
    run.scheduleOnce('afterRender', () => {
      // refreshing host details page or routing using url
      if (!machineId && selectedServerId) {
        request.registerPersistentStreamOptions({ socketUrlPostfix: selectedServerId, requiredSocketUrl: 'endpoint/socket' });
        redux.dispatch(resetDetailsInputAndContent());
      }
      if (sid) {
        // get host details
        // if endpointQuery serverId is already set and is equal to sid then
        // there is no point of calling registerPersistentStreamOptions again
        if (!selectedServerId || selectedServerId !== sid) {
          request.registerPersistentStreamOptions({ socketUrlPostfix: sid, requiredSocketUrl: 'endpoint/socket' });
          redux.dispatch(setSelectedEndpointServer(sid));
        }
        return request.ping('endpoint-server-ping')
          .then(() => {
            const { machineId, tabName = 'OVERVIEW', subTabName, pid } = params;
            if (machineId) {
              this.set('contextualHelp.topic', this.get(HELP_ID_MAPPING[tabName]));
            } else {
              this.set('contextualHelp.topic', this.get('contextualHelp.invHosts'));
            }
            redux.dispatch(isEndpointServerOffline(false));
            redux.dispatch(initializeHostPage(params));
            // To redirect to the Process details panel in the process tab
            run(() => {
              if (tabName === 'PROCESS' && subTabName === 'process-details') {
                redux.dispatch(toggleProcessDetailsView({ pid: parseInt(pid, 10) }, true));
              }
            });
          })
          .catch(function() {
            redux.dispatch(isEndpointServerOffline(true));
          });
      }
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

  // On deactivating the route send the user left page action to cleanup the state if any
  deactivate() {
    const redux = this.get('redux');
    const request = lookup('service:request');
    redux.dispatch(userLeftListPage());
    redux.dispatch(resetDetailsInputAndContent()); // Clear the details input
    request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
    this.set('contextualHelp.topic', null);
  }
});
