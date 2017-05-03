import * as ACTION_TYPES from '../actions/types';
import { handleActions } from 'redux-actions';
import { contextDataParser } from 'context/helpers/context-data-parser';
import * as DataUtil from 'context/util/context-data-modifier';
import TabList from 'context/config/dynamic-tab';

const initialState = {
  meta: null,
  lookupKey: null,
  activeTabName: null,
  dataSources: null,
  errorMessage: null,
  toolbar: null,
  lookupData: [],
  tabs: null
};

const context = handleActions({
  [ACTION_TYPES.INITIALIZE_CONTEXT_PANEL]: (state, { payload }) => ({
    ...state,
    activeTabName: null,
    dataSources: null,
    errorMessage: null,
    lookupData: [],
    tabs: null,
    toolbar: TabList.find((tab) => tab.tabType === payload.meta).toolbar,
    ...payload
  }),
  [ACTION_TYPES.UPDATE_ACTIVE_TAB]: (state, { payload }) => ({
    ...state,
    activeTabName: DataUtil.getTabEnabled(state.lookupData, payload) ? payload : state.activeTabName
  }),
  [ACTION_TYPES.GET_ALL_DATA_SOURCES]: (state, { payload }) => ({
    ...state,
    dataSources: payload,
    tabs: DataUtil.getTabs(state.meta, payload)
  }),
  [ACTION_TYPES.CONTEXT_ERROR]: (state, { payload }) => ({
    ...state,
    errorMessage: payload
  }),
  [ACTION_TYPES.GET_LOOKUP_DATA]: (state, { payload }) => ({
    ...state,
    lookupData: [].concat(contextDataParser([payload, state.lookupData])),
    activeTabName: DataUtil.getActiveTabName(state.activeTabName, payload),
    errorMessage: DataUtil.noDataToDisplayMessage(state.dataSources, state.lookupData)
  })
}, initialState);

export default context;