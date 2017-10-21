import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';


const initialState = Immutable.from({
  filter: {},
  areFilterLoading: null,
  activeFilter: null,
  expressionList: null,
  lastFilterAdded: null
});


const _handleSystemFilter = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { appliedHostFilter } = state;
    let defaultSearch;

    // arranging filter options, system filter then custom
    const systemFilters = [];
    const customFilters = [];
    const expressionList = [];
    for (const d of data) {
      if (d.filterType === 'MACHINE') {
        if (d.systemFilter) {
          systemFilters.pushObject(d);
        } else {
          customFilters.pushObject(d);
        }
      }
    }

    systemFilters.pushObjects(customFilters);

    // if request came from manage saved queries, we need to load
    if (appliedHostFilter) {
      defaultSearch = systemFilters.findBy('id', appliedHostFilter);
    } else {
      for (const d of data) {
        if (d.id === 'all') {
          defaultSearch = d;
          break;
        }
      }
    }
    return state.merge({
      filters: systemFilters,
      filterSelected: defaultSearch,
      customSearchVisible: !defaultSearch.systemFilter,
      expressionList
    });
  };
};


const _handleCreateSearch = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { filters } = state;
    const newFilter = [...filters];
    let position = -1;
    for (let i = 0; i < newFilter.length; i++) {
      if (newFilter[i].name === data.name) {
        position = i;
      }
    }
    if (position > -1) {
      newFilter.replace(position, 1, data);
    } else {
      newFilter.insertAt(newFilter.length - 1, data);
    }
    return state.merge({
      filters: newFilter,
      filterSelected: data
    });
  };
};


const _addToExpressionList = (expressionList, expression) => {
  const tempExpressionList = expressionList.asMutable({ deep: false });
  const newArray = tempExpressionList.slice();
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

  [ACTION_TYPES.RESET_HOST_FILTERS]: (state) => state.merge({
    activeFilter: null,
    areFilterLoading: 'wait',
    isFilterReset: true,
    expressionList: []
  }),

  [ACTION_TYPES.ADD_HOST_FILTER]: (state, { payload }) => state.merge({
    lastFilterAdded: payload.propertyName,
    expressionList: _addToExpressionList(state.expressionList, payload)
  }),

  [ACTION_TYPES.UPDATE_HOST_FILTER]: (state, { payload }) => state.merge({
    areFilterLoading: 'sorting',
    isFilterReset: false,
    lastFilterAdded: null,
    expressionList: _updateExpressionList(state.expressionList, payload)
  }),

  [ACTION_TYPES.REMOVE_HOST_FILTER]: (state, { payload }) => state.merge({
    areFilterLoading: 'sorting',
    lastFilterAdded: null,
    expressionList: state.expressionList.filter((item) => item.propertyName !== payload)
  }),

  [ACTION_TYPES.ADD_SYSTEM_FILTER]: (state, { payload }) => state.merge({
    areFilterLoading: 'sorting',
    expressionList: [payload]
  }),

  [ACTION_TYPES.SET_ACTIVE_FILTER]: (state, { payload }) => state.set('activeFilter', payload),


  [ACTION_TYPES.FETCH_ALL_SCHEMAS]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.merge({ schemas: action.payload.data.fields.filterBy('searchable', true) })
    });
  },

  [ACTION_TYPES.FETCH_ALL_FILTERS]: (state, action) => {
    return handle(state, action, {
      success: _handleSystemFilter(action)
    });
  },

  [ACTION_TYPES.CREATE_CUSTOM_SEARCH]: (state, action) => {
    return handle(state, action, {
      success: _handleCreateSearch(action)
    });
  },

  [ACTION_TYPES.SET_APPLIED_HOST_FILTER]: (state, { payload: { filterId, isCustomFilter } }) => state.merge({
    customSearchVisible: isCustomFilter,
    appliedHostFilter: filterId
  }),

  [ACTION_TYPES.UPDATE_FILTER_LIST ]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const { payload: { data } } = action;
        const filtersList = [...state.fileFilters, data ];
        return s.merge({ fileFilters: filtersList });
      }
    });
  }

}, initialState);

export default filterReducer;
