import { isEmpty } from 'ember-utils';
import run from 'ember-runloop';
import { userLeftListPage, setAppliedHostFilter, resetDetailsInputAndContent } from 'investigate-hosts/actions/ui-state-creators';
import { getAllSchemas } from 'investigate-hosts/actions/data-creators/host';
import { initializeAgentDetails, changeDetailTab } from 'investigate-hosts/actions/data-creators/details';
import service from 'ember-service/inject';
import Route from 'ember-route';

export default Route.extend({

  i18n: service(),

  redux: service(),

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

  model({ machineId, filterId, tabName = 'OVERVIEW' }) {
    const redux = this.get('redux');
    // @workaround We want to fire data actions when model changes. That won't work in Safari & Firefox if you are
    // transitioning from another route (e.g., `incidents`); only works if you are coming directly to this route from
    // a url/bookmark. As a workaround, use `run.next` to let the route transition finish before firing redux actions.
    run.next(() => {
      // On clicking the host name setting the machineId in the URL, on close removing the it from url
      if (machineId && !isEmpty(machineId)) {
        redux.dispatch(initializeAgentDetails({ agentId: machineId }, true));
        redux.dispatch(changeDetailTab(tabName));
      } else {
        // Resetting the details data and input data
        redux.dispatch(resetDetailsInputAndContent());
      }
      if (filterId && !isEmpty(filterId)) {
        redux.dispatch(setAppliedHostFilter(filterId, true));
      }
    });
    return {
      machineId
    };
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
