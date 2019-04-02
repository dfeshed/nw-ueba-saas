import { remainingMetaKeyBatches, initMetaKeyStates, isMetaStreaming } from 'investigate-events/reducers/investigate/meta/selectors';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import * as ACTION_TYPES from './types';
import executeMetaValuesRequest from './fetch/fetch-meta';
import { addGuidedPill } from 'investigate-events/actions/guided-creators';
import { buildMetaValueStreamInputs } from './utils';
import quote from 'investigate-events/util/quote';

const STREAM_LIMIT = 1000;
const STREAM_BATCH = 19;
// Maximum number of parallel threads that fetch meta values.
const MAX_CONCURRENT_REQUESTS = 2;


/**
 * Prepares meta array on init
 * Constructs an array of metaKeys(candidates) that haven't being fetched yet.
 * Kicks off stream requests for each value of a given set of metaKeys.
 * @private
 */
const metaGet = (init = false) => {
  return (dispatch, getState) => {

    // Safety measures ensuring all of the required information to
    // make a meta call is in place. If not, then escape.
    const state = getState();
    const { dictionaries: { language } } = state.investigate;
    const queryNode = getActiveQueryNode(state);
    if (!queryNode && !language) {
      return;
    }

    if (init) {
      dispatch(_initMeta());
    }

    const metaKeyStates = remainingMetaKeyBatches(getState());
    if (!metaKeyStates.length) {
      return;
    }

    // Because we have a cap on max threads, we need to batch our requests.
    // Because of metaGet's recursive behavior, we will have max threads on init,
    // and 1 thread all other times.
    let candidates;
    if (init) {
      candidates = metaKeyStates.slice(0, MAX_CONCURRENT_REQUESTS);
    } else {
      candidates = metaKeyStates.slice(0, 1);
    }

    const { investigate: { queryNode: { currentQueryHash } } } = state;
    candidates.forEach((metaKeyState) => dispatch(_metaKeyValuesGet(metaKeyState, currentQueryHash)));

  };
};

/**
 * Calls metaGet recursively until the queryHash changes.
 * @private
 */
function _metaKeyValuesGet(metaKeyState, queryHash) {
  return (dispatch, getState) => {

    const queryNode = getActiveQueryNode(getState());
    const { investigate: { dictionaries: { language }, meta: { options } } } = getState();
    const { metaName } = metaKeyState.info;
    const inputs = buildMetaValueStreamInputs(
      metaName,
      queryNode,
      language,
      options,
      STREAM_LIMIT,
      STREAM_BATCH
    );

    // why is onComplete never called?
    const handlers = {
      onInit() {
        dispatch({
          type: ACTION_TYPES.INIT_STREAM_FOR_META,
          payload: {
            keyName: metaName,
            value: {
              data: [],
              status: 'streaming',
              percent: 50
            }
          }
        });
      },
      onResponse(response) {
        const valueProps = {};
        if (response.data && response.data.length) {
          // Meta Values call *sometimes* returns "partial" results while still computing results.
          // So when we get values back, replace whatever the previous set of values were; don't append to them.
          valueProps.data = response.data;
        }

        if (response.meta) {
          valueProps.description = response.meta.description;
          valueProps.complete = response.meta.complete;
        }

        // Because this is a stream request, results are fetched partially.
        // In order to optimize reducer calls, I've tried to send response
        // action once its complete.
        // This reduces the actions from 3 to 1 action per response.
        // Side effect, we loose the real time description messages
        // and the percent fetched.
        // Right now, percent has been mocked to create an illusion.
        // Need some perspective here.

        if (valueProps.complete) {

          // If currentQueryHash does not equal queryHash that was passed in
          // when request was initiated, this means a new query has been
          // executed, exit recursion.
          const { currentQueryHash } = getState().investigate.queryNode;
          if (queryHash !== currentQueryHash) {
            return;
          }
          valueProps.status = 'complete';
          valueProps.percent = 100;
          dispatch({
            type: ACTION_TYPES.SET_META_RESPONSE,
            payload: {
              keyName: metaName,
              valueProps
            }
          });
          dispatch(metaGet());
        }
      }
    };

    executeMetaValuesRequest(inputs, handlers);
  };
}

const _initMeta = () => {
  return (dispatch, getState) => {
    const metaKeyStates = initMetaKeyStates(getState());
    dispatch({
      type: ACTION_TYPES.RESET_META_VALUES,
      payload: { metaKeyStates }
    });
  };
};

const createPillOnMetaDrill = ({ meta, value }) => {
  return (dispatch, getState) => {
    const { dictionaries: { language } } = getState().investigate;

    const metaKeyState = language.find((m) => m.metaName === meta);
    if (metaKeyState.format === 'Text' && value) {
      value = quote(String(value));
    } else if (value) {
      value = String(value);
    }

    const pillData = { meta, operator: '=', value };
    // adding new pill to end of current list of pills
    const position = getState().investigate.queryNode.pillsData.length;

    // Need a check here to see if that filter is already applied.
    // Rip out logic from the component.
    dispatch(addGuidedPill({ pillData, position, shouldAddFocusToNewPill: false }));
  };
};

/**
 * Sets flag for a metaKey
 * If streaming is already in process, it will be picked by
 * in the next iteration. Otherwise we dispatch action to
 * fetch that one meta.
 * @public
 */
const toggleMetaGroupOpen = (metaKeyInfo) => {
  return (dispatch, getState) => {

    const isMetaKeyOpen = !metaKeyInfo.isOpen;

    dispatch({
      type: ACTION_TYPES.TOGGLE_META_FLAG,
      payload: { metaKey: metaKeyInfo.metaName, isMetaKeyOpen }
    });

    // If meta key is newly opened, may need to fetch data
    if (isMetaKeyOpen) {
      const { meta } = getState().investigate.meta;
      const metaKeyState = meta.find((m) => m.info.metaName === metaKeyInfo.metaName);
      // if metaKeyState does not already have values object (which means a req was initialized
      // for that metaKey) and there is no streaming currently active, fetch!
      if (!metaKeyState.values && !isMetaStreaming(getState())) {
        dispatch(metaGet());
      }
    }
  };
};

export {
  metaGet,
  createPillOnMetaDrill,
  toggleMetaGroupOpen
};