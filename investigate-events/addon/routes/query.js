import Route from 'ember-route';
import service from 'ember-service/inject';

import {
  initializeDictionaries,
  initializeServices
} from 'investigate-events/actions/data-creators';
import {
  setMetaPanelSize,
  setQueryParams,
  setReconPanelSize,
  setSelectedEvent,
  setReconOpen,
  setReconClosed
} from 'investigate-events/actions/interaction-creators';
import { navGoto } from 'investigate-events/actions/navigation-creators';
import { parseEventQueryUri } from 'investigate-events/actions/helpers/query-utils';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),
  redux: service(),
  _routing: service('-routing'),

  queryParams: {
    eventId: {
      refreshModel: true
    },
    metaPanelSize: {
      refreshModel: true, // execute route.model() when metaPanelSize changes
      replace: true       // prevents adding a new item to browser's history
    },
    reconSize: {
      refreshModel: true,
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
      // If there's an event, let's show the recon panel
      if (eventId && eventId !== -1) {
        this.get('redux').dispatch(setReconOpen());
      }
      // View sizing
      this.get('redux').dispatch(setMetaPanelSize(metaPanelSize));
      this.get('redux').dispatch(setReconPanelSize(reconSize));
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
    selectEvent(event, index) {
      const { reconSize } = this.get('redux').getState().investigate.data;
      const { sessionId } = event;
      this.get('redux').dispatch(setSelectedEvent(event, index));
      this.get('redux').dispatch(setReconOpen());
      // this.send('contextPanelClose');
      this.transitionTo({
        queryParams: {
          eventId: sessionId,
          reconSize,
          metaPanelSize: 'min'
        }
      });
    },

    metaPanelSize(size = 'default') {
      const { metaPanelSize } = this.get('redux').getState().investigate.data;
      if (metaPanelSize !== size) {
        this.transitionTo({ queryParams: { metaPanelSize: size } });
      }
    },

    reconClose() {
      this.get('redux').dispatch(setReconClosed());
      this.get('redux').dispatch(setSelectedEvent(null));
      this.transitionTo({ queryParams: { eventId: undefined, metaPanelSize: 'default' } });
    },

    reconSize(size = 'max') {
      const { reconSize } = this.get('redux').getState().investigate.data;
      if (reconSize !== size) {
        this.transitionTo({ queryParams: { reconSize: size } });
      }
    },

    reconLinkToFile(file = {}) {
      const { start, end } = file;
      let { query = '' } = file;

      // Remove surrounding quotes from query, if any
      const hasSurroundingQuotes = query.match(/^"(.*)"$/);
      if (hasSurroundingQuotes) {
        query = hasSurroundingQuotes[1];
      }

      if (query && start && end) {
        const { serviceId } = this.get('redux').getState().investigate.queryNode;
        const routing = this.get('_routing');
        const url = routing.generateURL(
          routing.get('currentRouteName'),
          [`${serviceId}/${start}/${end}/${query}`],
          { eventId: undefined }
        );
        window.open(url, '_blank');
      }
    },

    toggleReconSize() {
      const {
        isReconOpen,
        reconSize
      } = this.get('redux').getState().investigate.data;
      if (isReconOpen) {
        this.send('reconSize', (reconSize === 'max') ? 'min' : 'max');
      }
    },

    toggleSlaveFullScreen: (() => {
      let _size = '';
      return function() {
        const {
          isReconOpen,
          reconSize
        } = this.get('redux').getState().investigate.data;
        if (isReconOpen) {
          if (reconSize === 'full') {
            // Set to previous size
            this.send('reconSize', (_size === 'min') ? 'min' : 'max');
          } else {
            // save off previous size
            _size = reconSize;
            this.send('reconSize', 'full');
          }
        }
      };
    })()
  }
});
