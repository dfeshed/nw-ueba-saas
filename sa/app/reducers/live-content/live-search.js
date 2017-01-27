import * as ACTION_TYPES from 'sa/actions/live-content/types';
import { handleActions } from 'redux-actions';

// Baseline search criteria that returns the first page of all results
const searchCriteria = {
  pageSize: 50,
  pageNumber: 0,
  sort: null,
  medium: [],
  metaKeys: [],
  metaValues: []
};

const initialState = {
  resourceTypes: null,
  media: null,
  metaKeys: null,
  metaValues: null,
  categories: null,
  results: null,
  isLoadingResults: false,
  searchCriteria
};

const search = handleActions({

  [ACTION_TYPES.FETCH_RESOURCE_TYPES]: (state, { payload }) => ({
    ...state,
    resourceTypes: payload
  }),

  [ACTION_TYPES.FETCH_MEDIA]: (state, { payload }) => ({
    ...state,
    media: payload
  }),

  [ACTION_TYPES.FETCH_META_KEYS]: (state, { payload }) => ({
    ...state,
    metaKeys: payload
  }),

  [ACTION_TYPES.FETCH_META_VALUES]: (state, { payload }) => ({
    ...state,
    metaValues: payload
  }),

  [ACTION_TYPES.FETCH_CATEGORIES]: (state, { payload }) => ({
    ...state,
    categories: payload
  }),

  [ACTION_TYPES.UPDATE_SEARCH_CRITERIA]: (state, { payload }) => ({
    ...state,
    searchCriteria: {
      ...state.searchCriteria,
      ...payload
    }
  }),

  [ACTION_TYPES.RESET_SEARCH_CRITERIA]: (state) => ({
    ...state,
    searchCriteria
  }),

  [ACTION_TYPES.GO_TO_FIRST_PAGE]: (state) => ({
    ...state,
    searchCriteria: {
      ...state.searchCriteria,
      pageNumber: 0
    }
  }),

  [ACTION_TYPES.GO_TO_LAST_PAGE]: (state) => ({
    ...state,
    searchCriteria: {
      ...state.searchCriteria,
      pageNumber: state.results.totalPages > 1 ? state.results.totalPages - 1 : 0
    }
  }),

  [ACTION_TYPES.GO_TO_NEXT_PAGE]: (state) => ({
    ...state,
    searchCriteria: {
      ...state.searchCriteria,
      pageNumber: state.results.pageNumber + 1
    }
  }),

  [ACTION_TYPES.GO_TO_PREVIOUS_PAGE]: (state) => ({
    ...state,
    searchCriteria: {
      ...state.searchCriteria,
      pageNumber: state.results.pageNumber - 1
    }
  }),

  [ACTION_TYPES.FETCH_SEARCH_RESULTS_STARTED]: (state) => ({
    ...state,
    isLoadingResults: true
  }),

  [ACTION_TYPES.FETCH_SEARCH_RESULTS_SUCCESS]: (state, { payload }) => ({
    ...state,
    isLoadingResults: false,
    results: payload
  }),

  [ACTION_TYPES.FETCH_SEARCH_RESULTS_FAILURE]: (state) => ({
    ...state,
    isLoadingResults: false
  }),

  [ACTION_TYPES.FOCUS_RESOURCE]: (state, { payload }) => ({
    ...state,
    focusResource: payload
  }),

  [ACTION_TYPES.BLUR_RESOURCE]: (state) => ({
    ...state,
    focusResource: null
  })
}, initialState);

export default search;