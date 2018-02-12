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

const markQueryDirty = () => ({ type: ACTION_TYPES.MARK_QUERY_DIRTY });

export {
  markQueryDirty,
  validateIndividualQuery
};
