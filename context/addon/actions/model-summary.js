import * as ACTION_TYPES from './types';
import { lookup } from 'ember-dependency-lookup';

const getSummaryData = (model) => {
  return (dispatch) => {
    const callback = (type, id, status, records) => {
      dispatch({
        type: ACTION_TYPES.GET_SUMMARY_DATA,
        payload: records
      });
    };
    const context = lookup('service:context');
    context.summary([model], callback);
  };
};

export {
  getSummaryData
};
