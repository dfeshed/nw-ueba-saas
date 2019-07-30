import Route from '@ember/routing/route';
import { setDataForHostTab } from 'investigate-hosts/actions/data-creators/host-details';
import { setSelectedTabData, getFileSearchResults } from 'investigate-hosts/actions/data-creators/explore';
import { inject as service } from '@ember/service';

const HELP_ID_MAPPING = {
  'OVERVIEW': 'contextualHelp.invHostsOverview',
  'PROCESS': 'contextualHelp.invHostsProcess',
  'ANOMALIES': 'contextualHelp.invHostsAnomalies',
  'AUTORUNS': 'contextualHelp.invHostsAutoruns',
  'FILES': 'contextualHelp.invHostsFiles',
  'DRIVERS': 'contextualHelp.invHostsDrivers',
  'LIBRARIES': 'contextualHelp.invHostsLibraries',
  'SYSTEM': 'contextualHelp.invHostsSysInfo',
  'DOWNLOADS': 'contextualHelp.invHostsDownloads',
  'DOWNLOADS-MFT': 'contextualHelp.invHostsDownloads'
};

export default Route.extend({

  redux: service(),

  contextualHelp: service(),

  isPageReload: true,

  queryParams: {
    subTabName: {
      refreshModel: true
    },

    checksum: {
      refreshModel: true
    },

    scanTime: {
      refreshModel: true
    }
  },

  model(params) {
    const redux = this.get('redux');
    const { tabName, subTabName, checksum, scanTime, searchKey } = params;
    const isPageReload = this.get('isPageReload');
    const { sid, id } = this.modelFor('hosts.details');
    if (sid && id) {
      this.set('contextualHelp.topic', this.get(HELP_ID_MAPPING[tabName]));
      if (scanTime && checksum) {
        redux.dispatch(setSelectedTabData({ tabName, subTabName, scanTime, checksum, searchKey }));
      }
      // If it's a page reload the if there is a search key reload the search result
      if (isPageReload && searchKey) {
        redux.dispatch(getFileSearchResults(searchKey, true));
      }
      redux.dispatch(setDataForHostTab(id, tabName, subTabName));
    }
    return params;
  },

  activate() {
    this.set('isPageReload', false);
  },

  deactivate() {
    this.set('isPageReload', true);
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
