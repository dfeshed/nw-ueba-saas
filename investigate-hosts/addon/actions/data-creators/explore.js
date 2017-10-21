import { HostDetails } from '../api';
import * as ACTION_TYPES from '../types';
import { getHostFiles } from './files';
import Ember from 'ember';
const { Logger } = Ember;

/**
 * Subscribe to fetching the search results.
 * @public
 */
const getFileSearchResults = (filterStr) => {
  return (dispatch, getState) => {
    const { endpoint: { detailsInput: { agentId } } } = getState();
    const filterObj = {
      text: filterStr,
      agentId
    };
    HostDetails.getFileSearchResults(
      filterObj,
      {
        onInit: () => dispatch({ type: ACTION_TYPES.START_FILE_SEARCH }),
        onCompleted: () => dispatch({ type: ACTION_TYPES.FILE_SEARCH_END }),
        onResponse: (payload) => dispatch({ type: ACTION_TYPES.FILE_SEARCH_PAGE, payload }),
        onError: (err) => Logger.error('Error in file search', err)
      }
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
 * @method resetExploredResults
 * @public
 * @returns {Object}
 */
const resetExploredResults = () => ({ type: ACTION_TYPES.RESET_EXPLORED_RESULTS });

const resetExploreSearch = () => {
  return (dispatch) => {
    dispatch(resetExploredResults());
    dispatch(getHostFiles());
  };
};

export {
  getFileSearchResults,
  setSelectedTabData,
  resetExploreSearch
};