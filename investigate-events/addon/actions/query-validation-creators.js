import * as ACTION_TYPES from './types';
import validateQueryFragment from './fetch/query-validation';

const validateIndividualQuery = (filter, callback) => {
  return (dispatch, getState) => {
    const state = getState().investigate;

    const encodeFilter = encodeURIComponent(filter);
    const { serviceId } = state.queryNode;
    validateQueryFragment(serviceId, encodeFilter).then((response) => {
      callback(response.data);
    }).catch((error) => {
      callback(false, error.meta);
    });
  };
};

/**
 * Mark a query clean or dirty.
 * @param {boolean} [flag] Is the query dirty or clean?
 * @return {Object} A redux action creator
 * @public
 */
const dirtyQueryToggle = (flag = true) => ({
  type: ACTION_TYPES.MARK_QUERY_DIRTY,
  payload: flag
});

export {
  dirtyQueryToggle,
  validateIndividualQuery
};
