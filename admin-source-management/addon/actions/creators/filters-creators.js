import * as ACTION_TYPES from 'admin-source-management/actions/types';

const applyFilters = (reload, expressions, belongsTo) => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.APPLY_FILTER, payload: expressions, meta: { belongsTo } });
    dispatch(reload());
  };
};

const resetFilters = (belongsTo) => ({ type: ACTION_TYPES.RESET_FILTER, meta: { belongsTo } });

export {
  applyFilters,
  resetFilters
};
