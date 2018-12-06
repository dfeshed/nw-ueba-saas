import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/file-analysis/file-analysis-api';

const toggleFileAnalysisView = (payload) => ({ type: ACTION_TYPES.TOGGLE_FILE_ANALYZER, payload });
/**
 * An action creator for getting the saved filter information
 * @returns {function(*)}
 * @public
 */
const getFileAnalysisData = (data) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FETCH_FILE_ANALYZER_FILE_PROPERTIES_DATA,
      promise: api.getFileAnalysisData(data),
      meta: {
        onSuccess: () => {
          dispatch({
            type: ACTION_TYPES.TOGGLE_FILE_ANALYZER,
            payload: true
          });
        }
      }
    });
    // Needs to be uncommented once backend changes are merged.
    // dispatch({
    //   type: ACTION_TYPES.FETCH_FILE_ANALYZER_DATA,
    //   promise: api.getFileAnalysisStringFormatData(data)
    // });
  };
};

export {
  getFileAnalysisData,
  toggleFileAnalysisView
};
