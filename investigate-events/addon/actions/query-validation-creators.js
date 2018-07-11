import * as ACTION_TYPES from './types';

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
  dirtyQueryToggle
};
