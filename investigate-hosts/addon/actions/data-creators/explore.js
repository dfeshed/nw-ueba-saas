import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { debug } from '@ember/debug';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';

let stopItemsStream;
/**
 * Subscribe to fetching the search results.
 * @public
 */
const getFileSearchResults = (filterStr, hideResult) => {
  return (dispatch, getState) => {
    const { endpoint: { detailsInput: { agentId } }, endpointQuery: { serverId } } = getState();
    const filterObj = {
      text: filterStr,
      agentId
    };
    HostDetails.getFileSearchResults(
      filterObj,
      {
        onInit: (stopStream) => {
          stopItemsStream = stopStream;
          dispatch({ type: ACTION_TYPES.START_FILE_SEARCH });
          dispatch(toggleExploreSearchResults(!hideResult));
        },
        onResponse: (payload) => {
          const { meta } = payload;
          const { totalSnapshots, truncated } = meta;
          dispatch({ type: ACTION_TYPES.FILE_SEARCH_PAGE, payload });
          // Check have you got all the data
          const { endpoint: { explore: { fileSearchResults = [] } } } = getState();
          if (totalSnapshots === fileSearchResults.length) {
            stopItemsStream();
            dispatch({ type: ACTION_TYPES.FILE_SEARCH_END, payload: { isDataTruncated: truncated } });
          }
        },
        onError: (err) => {
          const debugError = JSON.stringify(err);
          debug(`onError: getFileSearchResults ${debugError}`);
        }
      }, serverId
    );
  };
};

/**
 * Action for setting selectedTab with selected explore's runAs
 * @method getFileSearchResults
 * @public
 * @returns {Object}
 */
const setSelectedTabData = (option) => ({ type: ACTION_TYPES.SELECTED_TAB_DATA, payload: option });

/**
 * Action for resetting the explore state
 * @method resetExploreSearch
 * @public
 * @returns {Object}
 */
const resetExploreSearch = () => ({ type: ACTION_TYPES.RESET_EXPLORED_RESULTS });

export {
  getFileSearchResults,
  setSelectedTabData,
  resetExploreSearch
};
