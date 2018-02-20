import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const fileListState = Immutable.from({
  filter: {},
  areFilesLoading: null,
  activeFilter: null,
  expressionList: null,
  lastFilterAdded: null,
  fileFilters: [],
  selectedFilterId: null,
  isSystemFilter: false
});

const _addToExpressionList = (expressionList, expression) => {
  const newArray = expressionList.slice().asMutable();
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

  [ACTION_TYPES.RESET_FILE_FILTERS]: (state) => state.merge({
    activeFilter: null,
    areFilesLoading: 'wait',
    isFilterReset: true,
    expressionList: []
  }),

  [ACTION_TYPES.GET_FILTER]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({
        'fileFilters': [],
        'areFilesLoading': 'sorting'
      }),
      failure: (s) => s.set('areFilesLoading', 'error'),
      success: (s) => {
        const { payload: { data } } = action;
        const filters = data.filter((filter) => (filter.filterType === 'FILE'));
        let expressionList = [];
        if (state.selectedFilterId && filters.length) {
          const filter = filters.findBy('id', state.selectedFilterId);
          if (filter) {
            expressionList = filter.criteria.expressionList;
          }
        }
        return s.merge({
          fileFilters: filters,
          expressionList,
          areFilesLoading: 'completed'
        });
      }
    });
  },

  [ACTION_TYPES.ADD_FILE_FILTER]: (state, { payload }) => state.merge({
    lastFilterAdded: payload.propertyName,
    expressionList: _addToExpressionList(state.expressionList, payload)
  }),

  [ACTION_TYPES.UPDATE_FILE_FILTER]: (state, { payload }) => state.merge({
    areFilesLoading: 'sorting',
    isFilterReset: false,
    lastFilterAdded: null,
    activeFilter: null,
    expressionList: _updateExpressionList(state.expressionList, payload)
  }),

  [ACTION_TYPES.REMOVE_FILE_FILTER]: (state, { payload }) => state.merge({
    areFilesLoading: 'sorting',
    lastFilterAdded: null,
    expressionList: state.expressionList.filter((item) => item.propertyName !== payload)
  }),

  [ACTION_TYPES.SET_ACTIVE_FILTER]: (state, { payload }) => state.set('activeFilter', payload),

  [ACTION_TYPES.SET_EXPRESSION_LIST]: (state, { payload }) => state.set('expressionList', payload),

  [ACTION_TYPES.UPDATE_FILTER_LIST]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const { payload: { data } } = action;
        const filtersList = [...state.fileFilters, data ];
        return s.merge({ fileFilters: filtersList });
      }
    });
  },

  [ACTION_TYPES.SET_APPLIED_FILES_FILTER]: (state, { payload }) => state.set('selectedFilterId', payload),

  [ACTION_TYPES.SET_SYSTEM_FILTER_FLAG]: (state, { payload }) => state.set('isSystemFilter', payload),

  [ACTION_TYPES.DELETE_FILTER]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const { payload } = action;
        const filters = s.fileFilters.filter((item) => item.id !== payload.data.id);
        return s.set('fileFilters', filters);
      }
    });
  }
}, fileListState);

export default filterReducer;
