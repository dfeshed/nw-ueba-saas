import * as ACTION_TYPES from '../actions/types';
import { handleActions } from 'redux-actions';
import { contextDataParser } from 'context/helpers/context-data-parser';
import { getTabs } from 'context/util/context-data-modifier';
import TabList from 'context/config/dynamic-tab';

const initialState = {
  meta: null,
  lookupKey: null,
  activeTabName: 'LIST',
  dataSources: null,
  errorMessage: null,
  toolbar: null,
  lookupData: [],
  tabs: null,
  entitiesMetas: null
};

const context = handleActions({
  [ACTION_TYPES.RESTORE_DEFAULT]: () => ({
    ...initialState
  }),
  [ACTION_TYPES.INITIALIZE_CONTEXT_PANEL]: (state, { payload }) => ({
    ...state,
    dataSources: null,
    errorMessage: null,
    lookupData: [],
    tabs: null,
    toolbar: TabList.find((tab) => tab.tabType === payload.meta).toolbar,
    ...payload
  }),
  [ACTION_TYPES.UPDATE_ACTIVE_TAB]: (state, { payload }) => ({
    ...state,
    activeTabName: payload
  }),
  [ACTION_TYPES.GET_ALL_DATA_SOURCES]: (state, { payload }) => ({
    ...state,
    dataSources: payload,
    tabs: getTabs(state.meta, payload)
  }),
  [ACTION_TYPES.GET_CONTEXT_ENTITIES_METAS]: (state, { payload }) => ({
    ...state,
    entitiesMetas: payload
  }),
  [ACTION_TYPES.CONTEXT_ERROR]: (state, { payload }) => ({
    ...state,
    errorMessage: payload
  }),
  [ACTION_TYPES.GET_LOOKUP_DATA]: (state, { payload }) => {
    const lookupData = [].concat(contextDataParser([payload, state.lookupData]));
    return {
      ...state,
      lookupData
    };
  }
}, initialState);

export default context;