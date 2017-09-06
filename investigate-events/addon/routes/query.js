import { isEmpty } from 'ember-utils';
import Route from 'ember-route';
import run from 'ember-runloop';
import { parseEventQueryUri } from 'investigate-events/actions/helpers/query-utils';
import service from 'ember-service/inject';

export default Route.extend({
  contextualHelp: service(),

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
    const filterAttrs = parseEventQueryUri(params.filter);
    this.set('filterAttrs', filterAttrs);

    // @workaround Using `this.send()` throws an error if you are navigating to
    // this route directly from a bookmark. Ember tells us to use
    // `transition.send()` in that case instead. We could, but then any
    // sub-actions called by our initial action would not know to use
    // `transition` instead. So instead we use `run.next()` to wait until the
    // route has transitioned before calling any actions.
    run.next(() => {
      // Apply the route URL queryParams to the state model.
      this.send('metaPanelSizeReceived', params.metaPanelSize);
      this.send('reconSizeReceived', params.reconSize);
      this.send('navFindOrAdd', filterAttrs);

      const endpointId = state.get('queryNode.value.definition.serviceId');
      const { eventId } = params;

      if (!isEmpty(endpointId) && !isEmpty(eventId) && eventId !== -1) {
        this.send('reconOpen', endpointId, eventId);
      }
    });
    return state;
  },

  actions: {
    selectEvent(item, index) {
      const state = this.modelFor('application');
      const endpointId = state.get('queryNode.value.definition.serviceId');
      const { metas, sessionId } = item;
      this.send('reconOpen', endpointId, sessionId, metas, index);
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
