import Route from 'ember-route';
import service from 'ember-service/inject';

import {
  initializeInvestigate
} from 'investigate-events/actions/data-creators';
import {
  setMetaPanelSize,
  setQueryParams,
  setReconPanelSize,
  setSelectedEvent,
  setReconOpen,
  setReconClosed
} from 'investigate-events/actions/interaction-creators';
import { parseEventQueryUri } from 'investigate-events/actions/helpers/query-utils';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),
  redux: service(),
  _routing: service('-routing'),

  queryParams: {
    eventId: {
      refreshModel: false
    },
    metaPanelSize: {
      refreshModel: false, // Don't execute route.model() when metaPanelSize
      replace: true        // changes for now. Re-evaluate this when we enable
    },                     // meta panel.
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
    }
  },

  /**
   * Returns the app state model from the parent route. Is also responsible for
   * parsing the incoming query params and ensuring that the incoming query is
   * included in the app state.
   * @param {object} params
   * @returns {object} The state model from the parent route
   * @public
   */
  model(params) {
    // set query params to state
    const { eventId, metaPanelSize, reconSize } = params;
    const qp = parseEventQueryUri(params.filter);
    qp.sessionId = eventId;
    this.get('redux').dispatch(setQueryParams(qp));
    this.get('redux').dispatch(setMetaPanelSize(metaPanelSize));
    this.get('redux').dispatch(setReconPanelSize(reconSize));
    if (eventId && eventId !== -1) {
      this.get('redux').dispatch(setReconOpen());
    }
    this.get('redux').dispatch(initializeInvestigate());
    return this.modelFor('application');
  },

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
        this.get('redux').dispatch(setMetaPanelSize(size));
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
        this.get('redux').dispatch(setReconPanelSize(size));
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
