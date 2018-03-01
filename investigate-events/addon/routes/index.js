import Route from 'ember-route';
import service from 'ember-service/inject';
import { initializeInvestigate } from 'investigate-events/actions/initialization-creators';
import {
  setMetaPanelSize,
  setQueryFilterMeta,
  setReconClosed,
  setReconOpen,
  setReconPanelSize
} from 'investigate-events/actions/interaction-creators';
import { dirtyQueryToggle } from 'investigate-events/actions/query-validation-creators';
import {
  serializeQueryParams,
  uriEncodeMetaFilters
} from 'investigate-events/actions/utils';
import {
  META_PANEL_SIZES,
  RECON_PANEL_SIZES
} from 'investigate-events/constants/panelSizes';

export default Route.extend({
  contextualHelp: service(),
  redux: service(),
  _routing: service('-routing'),

  /**
   * The `queryParams` property controls how changes to query params in the URL
   * effect application or browser state.
   *
   * If we set `refreshModel` to `true`, it will re-run all the
   * model hooks. This will effectively look like a page refresh. We refresh
   * the model for parameters that will effect the contents of the data table.
   *
   * If `replace` is set to `true`, then the browser's history will be
   * over-written when that query param changes.
   * @public
   */
  queryParams: {
    sid: { refreshModel: true }, // serviceId
    st: { refreshModel: true },  // startTime
    et: { refreshModel: true },  // endTime
    mf: { refreshModel: true },  // metaFilters
    mps: { replace: true },      // metaPanelSize
    rs: { replace: true }        // reconSize
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.investigateModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.invEventAnalysis'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  },

  model(params) {
    /* If all the key values of 'params' are 'undefined',
       then hardReset is set to true and initial state is set. */
    const uniqParamValues = Object.values(params).uniq();
    const hardReset = uniqParamValues.length === 1 && uniqParamValues[0] === undefined;

    this.get('redux').dispatch(initializeInvestigate(params, hardReset));
  },

  actions: {
    executeQuery(metaFilters, externalLink = false) {
      metaFilters = metaFilters.filterBy('saved', true);
      const redux = this.get('redux');
      // Save the metaFilters to state
      const { data, queryNode } = redux.getState().investigate;
      const qp = {
        et: queryNode.endTime,
        mf: uriEncodeMetaFilters(metaFilters),
        mps: data.metaPanelSize,
        rs: data.reconSize,
        sid: queryNode.serviceId,
        st: queryNode.startTime
      };

      // Mark query as clean since we're submitting the query
      redux.dispatch(dirtyQueryToggle(false));

      if (externalLink) {
        if (qp.mf) {
          qp.mf = encodeURIComponent(qp.mf);
        }
        const query = serializeQueryParams(qp);
        const { location } = window;
        const path = `${location.origin}${location.pathname}?${query}`;
        window.open(path, '_blank');
      } else {
        qp.eid = undefined;
        this.send('reconClose');
        redux.dispatch(setQueryFilterMeta(metaFilters));
        this.transitionTo({ queryParams: qp });
      }
    },

    reconLinkToFile(file) {
      if (!file) {
        return;
      }
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
        // TODO - GTB
        const url = routing.generateURL(
          routing.get('currentRouteName'),
          [`${serviceId}/${start}/${end}/${query}`],
          { eventId: undefined }
        );
        window.open(url, '_blank');
      }
    },

    selectEvent(event) {
      const redux = this.get('redux');
      const { reconSize } = redux.getState().investigate.data;
      const { sessionId } = event;
      redux.dispatch(setReconOpen(event));
      this.send('contextPanelClose');
      this.transitionTo({
        queryParams: {
          eid: sessionId,
          rs: reconSize,
          mps: META_PANEL_SIZES.MIN
        }
      });
    },


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

    metaPanelSize(size = META_PANEL_SIZES.DEFAULT) {
      const redux = this.get('redux');
      const { metaPanelSize } = redux.getState().investigate.data;
      if (metaPanelSize !== size) {
        redux.dispatch(setMetaPanelSize(size));
        this.transitionTo({ queryParams: { mps: size } });
      }
    },

    reconSize(size = RECON_PANEL_SIZES.MAX) {
      const redux = this.get('redux');
      const { reconSize } = redux.getState().investigate.data;
      if (reconSize !== size) {
        redux.dispatch(setReconPanelSize(size));
        this.transitionTo({ queryParams: { rs: size } });
      }
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

    reconClose() {
      this.get('redux').dispatch(setReconClosed());
      this.transitionTo({
        queryParams: {
          eid: undefined,
          mps: META_PANEL_SIZES.DEFAULT
        }
      });
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
