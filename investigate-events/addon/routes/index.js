import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { later, debounce } from '@ember/runloop';
import { initializeInvestigate, queryIsRunning } from 'investigate-events/actions/initialization-creators';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import { updateSummaryData } from 'investigate-events/actions/data-creators';
import {
  setMetaPanelSize,
  setReconClosed,
  setReconOpen,
  setEventsTableSessionId,
  setReconPanelSize
} from 'investigate-events/actions/interaction-creators';
import { isSearchTerm, metaFiltersAsString, injectLogicalOperatorIfMissing } from 'investigate-events/util/query-parsing';
import { serializeQueryParams } from 'investigate-shared/utils/pivot-util';
import {
  META_PANEL_SIZES,
  RECON_PANEL_SIZES
} from 'investigate-events/constants/panelSizes';
import { hasMinimumCoreServicesVersionForColumnSorting } from '../reducers/investigate/services/selectors';
import { hasInvalidPill, isPillValidationInProgress } from '../reducers/investigate/query-node/selectors';
import { teardownNotifications, initializeNotifications } from '../actions/notification-creators';
import { replaceAllGuidedPills } from 'investigate-events/actions/pill-creators';
import { removeEmptyParens } from 'investigate-shared/actions/api/events/utils';
import { findSelectedPills } from 'investigate-events/actions/pill-utils';
import { OPERATOR_AND } from 'investigate-events/constants/pill';
import { RECON_EVENT_UPDATE_WAIT_TIME } from 'investigate-events/constants/event-recon';

const SUMMARY_CALL_INTERVAL = 60000;
const NOTIFICATION_SUBSCRIPTION_INTERVAL = 1866000;
let timerId, notificationsSchedulerId;

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
   *
   * Might want to take a peek at component-lib/utils/build-url (right-click actions)
   * while adding/removing params from here.
   * @public
   */
  queryParams: {
    pdhash: { refreshModel: true }, // pill data hashes
    sid: { refreshModel: true }, // serviceId
    st: { refreshModel: true }, // startTime
    et: { refreshModel: true }, // endTime
    mf: { refreshModel: true }, // pillData
    mps: { refreshModel: false }, // metaPanelSize
    sm: { refreshModel: false }, // start meta
    em: { refreshModel: false }, // end meta
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
    // TODO Nehal to revise this to subscribe to notifications when connection is lost
    // Scheduler for subscribing to notifications queue for every new websocket connection
    // in intervals of 31 minutes
    notificationsSchedulerId = setInterval(() => this.get('redux').dispatch(initializeNotifications()), NOTIFICATION_SUBSCRIPTION_INTERVAL);
  },

  deactivate() {
    this.get('request').disconnectNamed('investigate-events-event-stream');
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
    // terminate the continous polling of summary
    clearInterval(timerId);
    clearInterval(notificationsSchedulerId);
    const { notifications: { stopNotifications } } = this.get('redux').getState().investigate;
    if (stopNotifications) {
      stopNotifications();
      this.get('redux').dispatch(teardownNotifications());
    }
  },

  model(params) {
    const redux = this.get('redux');
    const state = redux.getState();

    // If nextQueryParams is present, we got here via internal
    // querying and do not need to re-run the query. Do need to
    // clean up nextQueryParams though.
    if (this.get('nextQueryParams')) {
      this.set('nextQueryParams', null);
    } else {
      // Set nextQueryParams to the incoming params object so that we don't
      // run the query a second time after getting the pill hash

      // do not set nextQueryParams if missing sort params
      // expecting a redirect with sort params after fetching prefs
      // also check if mixed mode with version less than 11.4
      // to prevent call to runInvestigateQuery below a second time

      if ((params.sortField && params.sortDir)) {
        this.set('nextQueryParams', params);
      }

      const notSortQuery = !state.investigate.data.isQueryExecutedBySort;
      const sortButQueryRequired = state.investigate.data.isQueryExecutedBySort && hasMinimumCoreServicesVersionForColumnSorting(state) && resultCountAtThreshold(state);
      const noQueryYet = !params.sid;
      if (
        notSortQuery ||
        sortButQueryRequired ||
        noQueryYet
      ) {
        this.runInvestigateQuery(params, false);
      }
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
      // insert it into the correct location in the pdhash below. We're splitting
      // on all the things that can be used to separate pills. " && ", " AND ",
      // " || ", and " OR ".
      textFilter = params.mf.split(/ &{2} | AND | \|{2} | OR /).find((d, i) => {
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
    executeQuery(externalLinkProps) {
      const isExternalLink = (externalLinkProps && externalLinkProps.externalLink) || false;
      const redux = this.get('redux');
      const state = redux.getState();
      const {
        data: { reconSize, sortField, sortDirection },
        meta: { metaPanelSize },
        queryNode: { endTime, pillsData, serviceId, startTime }
      } = state.investigate;

      // If we're not opening this query in a new window, start the query
      // process by notifying the UI so that it can show the cancel option.
      if (!isExternalLink) {
        redux.dispatch(queryIsRunning(true));
      }

      // If we're in the middle of validating the pill, let's defer processing
      // the request to execute the query. If validation has completed, check to
      // see if there are any invalid pills. Don't transition if this is true.
      if (isPillValidationInProgress(state)) {
        later(this, this.send, 'executeQuery', externalLinkProps, 50);
        return;
      } else if (hasInvalidPill(state)) {
        // Only exit if we're in guided mode and we have invalid pills
        redux.dispatch(queryIsRunning(false));
        return;
      }

      // It's possible that the user can craft a query that includes empty
      // paren sets. This is invalid query syntax, so we'll remove them if they
      // exist.
      const pillsDataWithoutEmptyParens = removeEmptyParens(pillsData);
      if (pillsData.length !== pillsDataWithoutEmptyParens.length) {
        // If there were empty parens, we need to re-render the pills with the
        // updated pillsData.
        redux.dispatch(replaceAllGuidedPills(pillsDataWithoutEmptyParens));
      }

      const qp = {
        eid: undefined,
        et: endTime,
        mf: metaFiltersAsString(pillsDataWithoutEmptyParens),
        mps: metaPanelSize,
        rs: reconSize,
        sid: serviceId,
        st: startTime,
        pdhash: undefined
      };

      if (hasMinimumCoreServicesVersionForColumnSorting(state) && sortDirection !== 'Unsorted') {
        qp.sortField = sortField;
        qp.sortDir = sortDirection;
      }

      if (isExternalLink) {
        const pills = findSelectedPills(pillsDataWithoutEmptyParens)
                      |> ((_) => injectLogicalOperatorIfMissing(_, OPERATOR_AND));
        if (pills.length > 0) { // if no selected pills in state, exit
          const pillString = metaFiltersAsString(pills);
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
      redux.dispatch(setEventsTableSessionId(event));
      // awaiting before transitioning to recon.
      // this will avoid calls to recon while traversing the event table.
      debounce(this, this.transitionToRecon, event, RECON_EVENT_UPDATE_WAIT_TIME);
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
  },
  transitionToRecon(event) {
    const redux = this.get('redux');
    redux.dispatch(setReconOpen(event));
    this.send('contextPanelClose');
    const state = redux.getState();
    const { reconSize, sortField, sortDirection } = state.investigate.data;
    const { sessionId } = event;
    const queryParams = {
      eid: sessionId,
      rs: reconSize,
      mps: META_PANEL_SIZES.MIN
    };

    if (hasMinimumCoreServicesVersionForColumnSorting(state)) {
      queryParams.sortField = sortField;
      queryParams.sortDir = sortDirection;
    }

    this.transitionTo({ queryParams });
  }
});
