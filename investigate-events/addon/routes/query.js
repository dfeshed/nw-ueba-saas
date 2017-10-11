import Route from 'ember-route';
import run from 'ember-runloop';
import service from 'ember-service/inject';

import {
  initializeDictionaries,
  initializeServices
} from 'investigate-events/actions/data-creators';
import {
  setMetaPanelSize,
  setQueryParams,
  setReconPanelSize,
  setSessionId
} from 'investigate-events/actions/interaction-creators';
import { navGoto } from 'investigate-events/actions/navigation-creators';
import { parseEventQueryUri } from 'investigate-events/actions/helpers/query-utils';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),
  redux: service(),

  queryParams: {
    eventId: {
      refreshModel: false
    },
    metaPanelSize: {
      refreshModel: true, // execute route.model() when metaPanelSize changes
      replace: true       // prevents adding a new item to browser's history
    },
    reconSize: {
      refreshModel: false,
      replace: true
    }
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.investigateModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.invEventAnalysis'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  },

  beforeModel() {
    // Re-route back to the parent's protected route if we don't have permission
    if (!this.get('accessControl.hasInvestigateAccess')) {
      this.transitionToExternal('protected');
    } else {
      // Get services
      this.get('redux').dispatch(initializeServices());
    }
  },

  /**
   * Returns the app state model from the parent route. Is also responsible for
   * parsing the incoming query params and ensuring that the incoming query is
   * included in the app state.
   * - Checks if a matching query is already in the app state's tree of queries;
   *   if not, adds it.
   * - Moves the app state's playhead to point to the query so it will presented
   *   to the end-user.
   * Note that we do not modify app state directly here; we merely dispatch
   * actions to request changes.
   * @param {object} params
   * @returns {object} The state model from the parent route
   * @public
   */
  model(params) {
    const state = this.modelFor('application');
    const { eventId, metaPanelSize, reconSize } = params;
    // Parse and save URL query params
    const filterAttrs = parseEventQueryUri(params.filter);
    filterAttrs.sessionId = eventId;
    this.set('filterAttrs', filterAttrs);
    const { serviceId } = filterAttrs;

    // Apply the route URL queryParams to Redux state
    if (serviceId) {
      this.get('redux').dispatch(setQueryParams(filterAttrs));
      // Get `language` and `aliases` now that we know what service we're using
      this.get('redux').dispatch(initializeDictionaries());
      // View sizing
      this.get('redux').dispatch(setMetaPanelSize(metaPanelSize));
      this.get('redux').dispatch(setReconPanelSize(reconSize));
    }

    if (serviceId && eventId && eventId !== -1) {
      run.next(() => {
        this.send('reconOpen', serviceId, eventId);
      });
    }
    // TEMP HACK - We can't continue down our execution path until we get
    // `aliases` and `language`, so check if they are present before
    // continuing.
    setTimeout(this._checkForDictionaries.bind(this), 50);
    return state;
  },

  // TEMP HACK - After fully converted to Redux, we will remove this.
  // This will run for about 5 seconds before timing out.
  _checkForDictionaries: (() => {
    let timeout = 100;
    return function() {
      --timeout;
      const { redux, filterAttrs } = this.getProperties('redux', 'filterAttrs');
      const { aliases, language } = redux.getState().investigate.dictionaries;
      if (aliases && language) {
        redux.dispatch(navGoto());
        this.send('navFindOrAdd', filterAttrs);
      } else if (timeout) {
        setTimeout(this._checkForDictionaries.bind(this), 50);
      }
    };
  })(),

  actions: {
    selectEvent(item, index) {
      const { serviceId } = this.get('redux').getState().investigate.queryNode;
      const { metas, sessionId } = item;
      this.get('redux').dispatch(setSessionId(sessionId));
      this.send('reconOpen', serviceId, sessionId, metas, index);
    },

    metaPanelSize(size = 'default') {
      const currentSize = this.get('redux').getState().investigate.metaPanelSize;
      if (currentSize === size) {
        return;
      }
      this.transitionTo({ queryParams: { metaPanelSize: size } });
    }
  }
});
