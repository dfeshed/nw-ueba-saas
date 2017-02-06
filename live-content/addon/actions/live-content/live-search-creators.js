import Ember from 'ember';
import * as ACTION_TYPES from './types';
import {
  fetchResourceTypes,
  fetchMedia,
  fetchMetaKeys,
  fetchMetaValues,
  fetchCategories,
  fetchResults
} from './fetch';

const { isBlank } = Ember;

const initializeDictionaries = () => {
  return (dispatch, getState) => {
    const { resourceTypes, media, metaKeys, metaValues, categories } = getState().live.search;

    _fetchDictionary(dispatch, resourceTypes, fetchResourceTypes, ACTION_TYPES.FETCH_RESOURCE_TYPES);
    _fetchDictionary(dispatch, media, fetchMedia, ACTION_TYPES.FETCH_MEDIA);
    _fetchDictionary(dispatch, metaKeys, fetchMetaKeys, ACTION_TYPES.FETCH_META_KEYS);
    _fetchDictionary(dispatch, metaValues, fetchMetaValues, ACTION_TYPES.FETCH_META_VALUES);
    _fetchDictionary(dispatch, categories, fetchCategories, ACTION_TYPES.FETCH_CATEGORIES);
  };
};

const updateSearchCriteria = (newCriteria = {}) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.UPDATE_SEARCH_CRITERIA,
      payload: newCriteria
    });

    dispatch(search());
  };
};

const firstPage = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GO_TO_FIRST_PAGE
    });

    dispatch(search());
  };
};

const lastPage = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GO_TO_LAST_PAGE
    });

    dispatch(search());
  };
};

const nextPage = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GO_TO_NEXT_PAGE
    });

    dispatch(search());
  };
};

const previousPage = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.GO_TO_PREVIOUS_PAGE
    });

    dispatch(search());
  };
};

const search = () => {
  return (dispatch, getState) => {
    const { searchCriteria } = getState().live.search;

    if (isBlank(searchCriteria)) {
      dispatch({
        type: ACTION_TYPES.RESET_SEARCH_CRITERIA
      });
    }

    dispatch({
      type: ACTION_TYPES.FETCH_SEARCH_RESULTS_STARTED
    });

    fetchResults(getState().live.search.searchCriteria).then((response) => {
      dispatch({
        type: ACTION_TYPES.FETCH_SEARCH_RESULTS_SUCCESS,
        payload: response.data || response
      });
    }).catch(() => {
      dispatch({
        type: ACTION_TYPES.FETCH_SEARCH_RESULTS_FAILURE
      });
    });
  };
};

const _fetchDictionary = (dispatch, data, promiseFunc, actionType) => {
  if (isBlank(data)) {
    promiseFunc().then((response) => {
      dispatch({
        type: actionType,
        payload: response.data || response
      });
    });
  }
};

export {
  initializeDictionaries,
  updateSearchCriteria,
  search,
  firstPage,
  lastPage,
  nextPage,
  previousPage
};