import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import Immutable from 'seamless-immutable';


const initialState = Immutable.from({
  fileSearchResults: [],
  searchValue: null,
  searchStatus: null,
  selectedTab: null,
  showSearchResults: false,
  componentName: 'host-detail/header/titlebar/explore/search-field'
});

const _handleFileSearchPage = (state, { payload: { data, meta = {}, request } }) => {
  const { fileSearchResults } = state;
  const newComponentName = 'host-detail/header/titlebar/explore/search-label';
  const searchKeyword = request.filter[0].value; // get the search keyword
  if (data.scanStartTime) {
    let newFileSearchResults = [...fileSearchResults];
    const streamData = {
      machineAgentId: data.machineAgentId,
      scanStartTime: data.scanStartTime,
      files: data.files
    };
    newFileSearchResults.push(streamData);
    newFileSearchResults = newFileSearchResults.sortBy('scanStartTime');
    newFileSearchResults.reverse();
    return state.merge({
      searchStatus: meta.complete ? 'complete' : 'wait',
      fileSearchResults: [...newFileSearchResults],
      searchValue: searchKeyword,
      componentName: newComponentName,
      showSearchResults: true
    });
  } else {
    return state.merge({
      showSearchResults: true,
      componentName: newComponentName,
      searchValue: searchKeyword,
      searchStatus: 'wait'
    });
  }
};

const hostSummary = reduxActions.handleActions({

  [ACTION_TYPES.FILE_SEARCH_PAGE]: _handleFileSearchPage,

  [ACTION_TYPES.START_FILE_SEARCH]: (state) => state.merge({ showSearchResults: true, searchStatus: 'wait' }),

  [ACTION_TYPES.FILE_SEARCH_END]: (state) => state.set('searchStatus', 'complete'),

  [ACTION_TYPES.SELECTED_TAB_DATA]: (state, { payload }) => state.set('selectedTab', payload),

  [ACTION_TYPES.TOGGLE_EXPLORE_SEARCH_RESULTS]: (state, { payload: { flag } }) => state.set('showSearchResults', flag),

  [ACTION_TYPES.RESET_EXPLORED_RESULTS]: (state) => state.merge(initialState),

  [ACTION_TYPES.RESET_INPUT_DATA]: (state) => state.merge(initialState)

}, initialState);

export default hostSummary;
