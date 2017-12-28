import Route from 'ember-route';
import service from 'ember-service/inject';

import {
  initializeInvestigate
} from 'investigate-events/actions/data-creators';

import {
  setMetaPanelSize,
  setReconPanelSize,
  setSelectedEvent,
  setReconOpen,
  setReconClosed,
  setQueryFilterMeta
} from 'investigate-events/actions/interaction-creators';

import { uriEncodeEventQuery, uriEncodeMetaFilterConditions } from 'investigate-events/actions/helpers/query-utils';

import {
  META_PANEL_SIZES,
  RECON_PANEL_SIZES
} from 'investigate-events/constants/panelSizes';

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

  /**
   * Returns the app state model from the parent route. Is also responsible for
   * parsing the incoming query params and ensuring that the incoming query is
   * included in the app state.
   * @param {object} params
   * @returns {object} The state model from the parent route
   * @public
   */
  model(params) {
    this.get('redux').dispatch(initializeInvestigate(params));
  },

  actions: {
    /**
     * Updates state in order to reveal the Context Panel UI and feed it the
     * type & ID of an entity to lookup.
     * @param {string} entityType One of configured entity types; e.g., 'IP',
     * 'HOST', 'USER', etc.
     * @param {string|number} entityId The ID of an entity.
     * @public
     */
    contextPanelOpen(entityType, entityId) {
      this.transitionTo({
        queryParams: {
          entityType,
          entityId
        }
      });
    },

    /**
     * Updates the UI state in order to hide the Context Panel UI.
     * @public
     */
    contextPanelClose() {
      this.transitionTo({
        queryParams: {
          entityType: undefined,
          entityId: undefined
        }
      });
    },

    executeQuery(filters, externalLink = false) {
      if (externalLink) {
        const state = this.get('redux').getState().investigate.queryNode;
        const query = `${state.serviceId}/${state.startTime}/${state.endTime}/${uriEncodeMetaFilterConditions(filters)}`;
        const path = `${window.location.origin}/investigate/events/query/${query}`;
        window.open(path, '_blank');
      } else {
        this.get('redux').dispatch(setQueryFilterMeta(filters));
        this.transitionTo('query', uriEncodeEventQuery(this.get('redux').getState().investigate.queryNode));
      }
    },

    metaPanelSize(size = META_PANEL_SIZES.DEFAULT) {
      const { metaPanelSize } = this.get('redux').getState().investigate.data;
      if (metaPanelSize !== size) {
        this.get('redux').dispatch(setMetaPanelSize(size));
        this.transitionTo({ queryParams: { metaPanelSize: size } });
      }
    },

    // TODO - Make this work when meta panel is reduxed
    // navDrill(queryNode, metaName, metaValue) {
    //   this.transitionTo('query', eventQueryUri([
    //     queryNode.get('value.definition'),
    //     metaName,
    //     metaValue
    //   ]));
    // },

    reconClose() {
      this.get('redux').dispatch(setReconClosed());
      this.get('redux').dispatch(setSelectedEvent(null));
      this.transitionTo({
        queryParams: {
          eventId: undefined,
          metaPanelSize: META_PANEL_SIZES.DEFAULT
        }
      });
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

    reconSize(size = RECON_PANEL_SIZES.MAX) {
      const { reconSize } = this.get('redux').getState().investigate.data;
      if (reconSize !== size) {
        this.get('redux').dispatch(setReconPanelSize(size));
        this.transitionTo({ queryParams: { reconSize: size } });
      }
    },

    selectEvent(event) {
      const { reconSize } = this.get('redux').getState().investigate.data;
      const { sessionId } = event;
      this.get('redux').dispatch(setSelectedEvent(event));
      this.get('redux').dispatch(setReconOpen());
      this.send('contextPanelClose');
      this.transitionTo({
        queryParams: {
          eventId: sessionId,
          reconSize,
          metaPanelSize: META_PANEL_SIZES.MIN
        }
      });
    },

    toggleReconSize() {
      const {
        isReconOpen,
        reconSize
      } = this.get('redux').getState().investigate.data;
      if (isReconOpen) {
        this.send('reconSize', (reconSize === RECON_PANEL_SIZES.MAX) ? RECON_PANEL_SIZES.MIN : RECON_PANEL_SIZES.MAX);
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
          if (reconSize === RECON_PANEL_SIZES.FULL) {
            // Set to previous size
            this.send('reconSize', (_size === RECON_PANEL_SIZES.MIN) ?
              RECON_PANEL_SIZES.MIN : RECON_PANEL_SIZES.MAX);
          } else {
            // save off previous size
            _size = reconSize;
            this.send('reconSize', RECON_PANEL_SIZES.FULL);
          }
        }
      };
    })()
  }
});
