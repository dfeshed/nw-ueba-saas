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
 * Marks a query dirty after checking if its not to avoid spamming
 * @param {boolean} [flag] Is the query dirty or clean?
 * @return {Object} A redux action thunk
 * @public
 */
const dirtyQueryToggle = (flag = true) => {
  return (dispatch, getState) => {
    const dirtyFlag = getState().investigate.queryNode.isDirty;
    if (flag !== dirtyFlag) {
      dispatch({
        type: ACTION_TYPES.MARK_QUERY_DIRTY,
        payload: flag
      });
    }
  };
};

export {
  dirtyQueryToggle,
  validateIndividualQuery
};
