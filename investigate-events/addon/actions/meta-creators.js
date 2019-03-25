import { remainingMetaKeyBatches, initMetaKeyStates, isMetaStreaming } from 'investigate-events/reducers/investigate/meta/selectors';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import * as ACTION_TYPES from './types';
import executeMetaValuesRequest from './fetch/fetch-meta';
import { addGuidedPill } from 'investigate-events/actions/guided-creators';
import { buildMetaValueStreamInputs } from './utils';

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

    // add onError
    // why is onComplete never called?
    const handlers = {
      onInit() {
        dispatch({
          type: ACTION_TYPES.INIT_STREAM_FOR_META,
          payload: {
            keyName: metaName,
            value: {
              data: [],
              status: 'streaming'
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
        valueProps.description = response.meta && response.meta.description;
        valueProps.complete = response.meta && response.meta.complete;
        const percent = response.meta && response.meta.percent;
        if (percent !== undefined) {
          valueProps.percent = percent;
        }

        if (valueProps.complete) {

          // If currentQueryHash does not equal queryHash that was passed in
          // when request was initiated, this means a new query has been
          // executed, exit recursion.
          const { currentQueryHash } = getState().investigate.queryNode;
          if (queryHash !== currentQueryHash) {
            return;
          }
          valueProps.status = 'complete'; // revisit status
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
    const pillData = { meta, operator: '=', value };
    // adding new pill to end of current list of pills
    const position = getState().investigate.queryNode.pillsData.length;

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
const toggleMetaGroupOpen = (metaKey) => {
  return (dispatch, getState) => {
    if (!metaKey.isOpen) {

      dispatch({
        type: ACTION_TYPES.TOGGLE_META_FLAG,
        payload: { metaKey }
      });

      if (!isMetaStreaming(getState())) {
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