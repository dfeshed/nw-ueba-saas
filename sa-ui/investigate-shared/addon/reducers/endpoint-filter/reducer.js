import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-shared/actions/types';

const fileListState = Immutable.from({
  selectedFilter: null,
  expressionList: [],
  savedFilterList: []
});

const isFilterSelected = (id, filter) => {
  return filter && filter.id === id;
};
const filterReducer = handleActions({

  [ACTION_TYPES.GET_FILTER]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const { payload: { data } } = action;
        const filters = data.filter((filter) => (filter.filterType === action.meta.belongsTo));
        let expressionList = [];
        const filter = s.selectedFilter;
        if (filter) {
          expressionList = filter.criteria.expressionList;
        }
        return s.merge({
          selectedFilter: filter,
          savedFilterList: filters,
          expressionList
        });
      }
    });
  },

  [ACTION_TYPES.APPLY_FILTER]: (state, { payload }) => state.set('expressionList', payload),

  [ACTION_TYPES.RESET_FILTER]: (state) => state.merge({
    'expressionList': [],
    'selectedFilter': { id: 1, criteria: { expressionList: [] } }
  }), // RESET To All

  [ACTION_TYPES.SET_SAVED_FILTER]: (state, { payload }) => state.merge({ selectedFilter: payload, expressionList: payload.criteria.expressionList }),

  [ACTION_TYPES.SAVE_FILTER]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const { payload: { data } } = action;
        const index = state.savedFilterList.findIndex((item) => item.id === data.id);
        let list = [...state.savedFilterList, data];
        if (index !== -1) {
          list = state.savedFilterList.set(index, data);
        }
        return s.merge({ 'savedFilterList': list, selectedFilter: data });
      }
    });
  },

  [ACTION_TYPES.DELETE_FILTER]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const { payload } = action;
        const isSelected = isFilterSelected(payload.data.id, s.selectedFilter);
        const savedFilterList = s.savedFilterList.filter((item) => item.id !== payload.data.id);
        const selectedFilter = isSelected ? null : s.selectedFilter;
        const expressionList = isSelected ? [] : s.expressionList;
        return s.merge({ savedFilterList, selectedFilter, expressionList });
      }
    });
  }
}, fileListState);

export default filterReducer;
