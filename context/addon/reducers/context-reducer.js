import * as ACTION_TYPES from '../actions/types';
import { handleActions } from 'redux-actions';
import { contextDataParser } from 'context/helpers/context-data-parser';
import { getActiveTabName, noDataToDisplayMessage, getTabs, needToDisplay } from 'context/util/context-data-modifier';
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
  [ACTION_TYPES.RESTORE_DEFAULT]: () => ({
    ...initialState
  }),
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
    activeTabName: needToDisplay(null, state.lookupData[0], { dataSourceGroup: payload }, state.dataSources) ? payload : state.activeTabName
  }),
  [ACTION_TYPES.GET_ALL_DATA_SOURCES]: (state, { payload }) => ({
    ...state,
    dataSources: payload,
    tabs: getTabs(state.meta, payload),
    errorMessage: noDataToDisplayMessage(payload, state.lookupData)
  }),
  [ACTION_TYPES.CONTEXT_ERROR]: (state, { payload }) => ({
    ...state,
    errorMessage: payload
  }),
  [ACTION_TYPES.GET_LOOKUP_DATA]: (state, { payload }) => {
    const lookupData = [].concat(contextDataParser([payload, state.lookupData]));
    return {
      ...state,
      lookupData,
      activeTabName: getActiveTabName(state.activeTabName, payload),
      errorMessage: noDataToDisplayMessage(state.dataSources, lookupData)
    };
  }
}, initialState);

export default context;