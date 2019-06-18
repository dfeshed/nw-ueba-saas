import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { later } from '@ember/runloop';
import { initializeInvestigate, queryIsRunning } from 'investigate-events/actions/initialization-creators';
import { updateSummaryData } from 'investigate-events/actions/data-creators';
import {
  setMetaPanelSize,
  setReconClosed,
  setReconOpen,
  setReconPanelSize
} from 'investigate-events/actions/interaction-creators';
import { isSearchTerm, uriEncodeMetaFilters } from 'investigate-events/util/query-parsing';
import { serializeQueryParams } from 'investigate-shared/utils/pivot-util';
import {
  META_PANEL_SIZES,
  RECON_PANEL_SIZES
} from 'investigate-events/constants/panelSizes';
import { hasMinimumCoreServicesVersionForColumnSorting } from '../reducers/investigate/services/selectors';
import { hasInvalidPill, isPillValidationInProgress } from '../reducers/investigate/query-node/selectors';
import { teardownNotifications, initializeNotifications } from '../actions/notification-creators';

const SUMMARY_CALL_INTERVAL = 60000;
let timerId;

export default Route.extend({
  contextualHelp: service(),
  redux: service(),
  request: service(),
  eventBus: service(),

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
    pdhash: { refreshModel: true }, // pill data hashes
    sid: { refreshModel: true }, // serviceId
    st: { refreshModel: true }, // startTime
    et: { refreshModel: true }, // endTime
    mf: { refreshModel: true }, // pillData
    mps: { refreshModel: false }, // metaPanelSize
    rs: { refreshModel: false }, // reconSize
    sortField: { refreshModel: true },
    sortDir: { refreshModel: true }
  },

  /**
   * Params used to update URL when hash comes in. Presence of nextQueryParams
   * means we need to update the URL, but not re-run the query
   */
  nextQueryParams: null,

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.investigateModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.invEventAnalysis'));
    // Scheduler for retrieving latest summaryData.
    // Helps in cases where the service hasn't been re-selected or the page has not been refreshed
    // in a long time.
    timerId = setInterval(() => this.get('redux').dispatch(updateSummaryData()), SUMMARY_CALL_INTERVAL);
    // dispatch call to open notification websocket
    this.get('redux').dispatch(initializeNotifications());
  },

  deactivate() {
    this.get('request').disconnectNamed('investigate-events-event-stream');
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
    // terminate the continous polling of summary
    clearInterval(timerId);
    const { notifications: { stopNotifications } } = this.get('redux').getState().investigate;
    if (stopNotifications) {
      stopNotifications();
      this.get('redux').dispatch(teardownNotifications());
    }
  },

  model(params) {
    // If nextQueryParams is present, we got here via internal
    // querying and do not need to re-run the query. Do need to
    // clean up nextQueryParams though.
    if (this.get('nextQueryParams')) {
      this.set('nextQueryParams', null);
    } else {
      // Set nextQueryParams to the incoming params object so that we don't
      // run the query a second time after getting the pill hash
      this.set('nextQueryParams', params);
      this.runInvestigateQuery(params, false);
    }
  },

  /**
   * Massages params and then kicks off the intialization of the
   * investigate query. isInternalQuery is an indication that this
   * query was kicked off via internal action, not via URL update.
   */
  runInvestigateQuery(params, isInternalQuery) {
    // If all the key values of 'params' are 'undefined',
    // then hardReset is set to true and initial state is set.
    const uniqParamValues = Object.values(params).uniq();
    const hardReset = uniqParamValues.length === 1 && uniqParamValues[0] === undefined;

    this.get('redux').dispatch(initializeInvestigate(params, this.transitionToPillHash.bind(this), hardReset, isInternalQuery));
  },

  /**
   * Pulls stored query params and merges with query hash, then transitions to
   * new URL. Ensures meta filter params are not in the URL.
   */
  transitionToPillHash(newHashes, nukeNextQP = true) {
    const params = this.get('nextQueryParams') || {};

    // Let hash be undefined if not passed in
    let textFilter, textFilterIdx, pdhash;

    if (params.mf) {
      // Look to the metaFilters for a Text filter and save off index so we can
      // insert it into the correct location in the pdhash below.
      textFilter = params.mf.split('/').find((d, i) => {
        textFilterIdx = i;
        return isSearchTerm(d);
      });
    }

    if (newHashes && textFilter) {
      // insert Text filter into correct location of hashes
      pdhash = [
        ...newHashes.slice(0, textFilterIdx),
        textFilter,
        ...newHashes.slice(textFilterIdx)
      ].join(',');
    } else if (textFilter) {
      pdhash = [textFilter];
    } else if (newHashes) {
      pdhash = newHashes;
    }

    if (nukeNextQP && !newHashes && !textFilter) {
      this.set('nextQueryParams', null);
    } else {
      this.transitionTo({
        queryParams: {
          ...params,
          pdhash,
          mf: undefined
        }
      });
    }
  },

  actions: {
    executeQuery(externalLink) {
      const redux = this.get('redux');
      const state = redux.getState();
      const {
        data: { reconSize, sortField, sortDirection },
        meta: { metaPanelSize },
        queryNode: { endTime, pillsData, queryView, serviceId, startTime }
      } = state.investigate;

      // If we're not opening this query in a new window, start the query
      // process by notifying the UI so that it can show the cancel option.
      if (!externalLink) {
        redux.dispatch(queryIsRunning(true));
      }

      // If we're in the middle of validating the pill, let's defer processing
      // the request to execute the query. If validation has completed, check to
      // see if there are any invalid pills. Don't transition if this is true.
      if (isPillValidationInProgress(state)) {
        later(this, this.send, 'executeQuery', externalLink, 50);
        return;
      } else if (queryView === 'guided' && hasInvalidPill(state)) {
        // Only exit if we're in guided mode and we have invalid pills
        redux.dispatch(queryIsRunning(false));
        return;
      }

      const qp = {
        eid: undefined,
        et: endTime,
        mf: uriEncodeMetaFilters(pillsData),
        mps: metaPanelSize,
        rs: reconSize,
        sid: serviceId,
        st: startTime,
        pdhash: undefined
      };

      if (hasMinimumCoreServicesVersionForColumnSorting(state)) {
        qp.sortField = sortField;
        qp.sortDir = sortDirection;
      }

      if (externalLink) {
        const selectedPills = pillsData.filter((pill) => pill.isSelected);
        if (selectedPills.length > 0) { // if no selected pills in state, exit
          const pillString = uriEncodeMetaFilters(selectedPills);
          qp.mf = encodeURIComponent(pillString);
          delete qp.eid; // delete unnecessary param, do not want recon to open
          const query = serializeQueryParams(qp);
          const { location } = window;
          const path = `${location.origin}${location.pathname}?${query}`;
          window.open(path, '_blank');
        }
      } else {
        this.get('eventBus').trigger('rsa-content-tethered-panel-hide-tableSearchPanel');
        redux.dispatch(setReconClosed());
        this.set('nextQueryParams', qp);
        this.runInvestigateQuery(qp, true);

        // If there are no meta at all, and the user has executed
        // the query, then there will be no pill hash, can/should
        // transition now with no hash passed in
        if (qp.mf === undefined) {
          this.transitionToPillHash(undefined, false);
        }
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
