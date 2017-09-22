import Route from 'ember-route';
import run from 'ember-runloop';
import service from 'ember-service/inject';

import {
  initializeDictionaries,
  initializeServices
} from 'investigate-events/actions/data-creators';
import { parseEventQueryUri } from 'investigate-events/actions/helpers/query-utils';
import { serviceSelected } from 'investigate-events/actions/interaction-creators';

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
      replace: true,      // prevents adding a new item to browser's history
      scope: 'controller' // lives beyond model scope
    },
    reconSize: {
      refreshModel: false,
      replace: true,
      scope: 'controller'
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
    // Parse and save URL query params
    const filterAttrs = parseEventQueryUri(params.filter);
    this.set('filterAttrs', filterAttrs);

    // @workaround Using `this.send()` throws an error if you are navigating to
    // this route directly from a bookmark. Ember tells us to use
    // `transition.send()` in that case instead. We could, but then any
    // sub-actions called by our initial action would not know to use
    // `transition` instead. So instead we use `run.next()` to wait until the
    // route has transitioned before calling any actions.
    run.next(() => {
      const { serviceId } = filterAttrs;
      const {
        eventId,
        metaPanelSize,
        reconSize
      } = params;

      // Apply the route URL queryParams to Redux state
      if (serviceId) {
        this.get('redux').dispatch(serviceSelected(serviceId));
        // Get `language` and `aliases` now that we know what service we're using
        this.get('redux').dispatch(initializeDictionaries());
      }

      // Apply the route URL queryParams to the state model.
      this.send('metaPanelSizeReceived', metaPanelSize);
      this.send('reconSizeReceived', reconSize);
      if (serviceId && eventId && eventId !== -1) {
        this.send('reconOpen', serviceId, eventId);
      }
      // TEMP HACK - We can't continue down our execution path until we get
      // `aliases` and `language`, so check if they are present before
      // continuing.
      setTimeout(this._checkForDictionaries.bind(this), 50);
    });
    return state;
  },

  // TEMP HACK - After fully converted to Redux, we will remove this.
  _checkForDictionaries() {
    const { redux, filterAttrs } = this.getProperties('redux', 'filterAttrs');
    const { aliases, language } = redux.getState().dictionaries;
    if (aliases && language) {
      this.send('navFindOrAdd', filterAttrs);
    } else {
      setTimeout(this._checkForDictionaries.bind(this), 50);
    }
  },

  actions: {
    selectEvent(item, index) {
      const state = this.modelFor('application');
      const serviceId = state.get('queryNode.value.definition.serviceId');
      const { metas, sessionId } = item;
      this.send('reconOpen', serviceId, sessionId, metas, index);
    },

    submitQuery(query) {
      const fa = this.get('filterAttrs');
      const uri = (fa.metaFilter.uri && fa.metaFilter.uri !== '') ?
                    fa.metaFilter.uri : null;
      // Navigate to new results
      this.transitionTo('query', [
        fa.serviceId,
        fa.startTime,
        fa.endTime,
        uri,
        query
      ].compact().join('/'));

      this.send('reconClose', true);
    }
  }
});
