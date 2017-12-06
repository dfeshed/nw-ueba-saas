import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeHostPage, getAllSchemas } from 'investigate-hosts/actions/data-creators/host';
import { userLeftListPage } from 'investigate-hosts/actions/ui-state-creators';
import { ping } from 'streaming-data/services/data-access/requests';
import run from 'ember-runloop';

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
      this.transitionToExternal('protected.permission-denied'); // Directing to permission denied page
    }
  },

  model(params) {
    return ping('endpoint-server-ping')
      .then(() => {
        const redux = this.get('redux');
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
  }
});
