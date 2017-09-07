import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const fileListState = {
  filter: {},
  areFilesLoading: null,
  activeFilter: null,
  expressionList: null,
  lastFilterAdded: null
};

const _addToExpressionList = (expressionList, expression) => {
  const newArray = expressionList.slice();
  newArray.splice(newArray.index, 0, expression);
  return newArray;
};

const _updateExpressionList = (expressionList, expression) => {
  if (!expressionList.findBy('propertyName', expression.propertyName)) {
    return _addToExpressionList(expressionList, expression);
  }
  return expressionList.map((item) => {
    if (item.propertyName !== expression.propertyName) {
      return item;
    }
    return {
      ...item,
      ...expression
    };
  });
};

const filterReducer = handleActions({

  [ACTION_TYPES.RESET_FILE_FILTERS]: (state) => ({
    ...state,
    activeFilter: null,
    areFilesLoading: 'wait',
    isFilterReset: true,
    expressionList: []
  }),

  [ACTION_TYPES.GET_FILTER]: (state) => ({
    ...state,
    expressionList: [] // Setting empty list of filter as save filter functionality not implemented
  }),

  [ACTION_TYPES.ADD_FILE_FILTER]: (state, { payload }) => ({
    ...state,
    lastFilterAdded: payload.propertyName,
    expressionList: _addToExpressionList(state.expressionList, payload)
  }),

  [ACTION_TYPES.UPDATE_FILE_FILTER]: (state, { payload }) => ({
    ...state,
    areFilesLoading: 'sorting',
    isFilterReset: false,
    lastFilterAdded: null,
    expressionList: _updateExpressionList(state.expressionList, payload)
  }),

  [ACTION_TYPES.REMOVE_FILE_FILTER]: (state, { payload }) => ({
    ...state,
    areFilesLoading: 'sorting',
    lastFilterAdded: null,
    expressionList: state.expressionList.filter((item) => item.propertyName !== payload)
  }),

  [ACTION_TYPES.ADD_SYSTEM_FILTER]: (state, { payload }) => ({
    ...state,
    areFilesLoading: 'sorting',
    expressionList: [ payload ]
  }),

  [ACTION_TYPES.SET_ACTIVE_FILTER]: (state, { payload }) => ({
    ...state,
    activeFilter: payload
  })

}, fileListState);

export default filterReducer;
