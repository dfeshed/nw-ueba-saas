import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/file-analysis/file-analysis-api';

const toggleFileAnalysisView = (payload) => ({ type: ACTION_TYPES.TOGGLE_FILE_ANALYZER, payload });
/**
 * An action creator for getting the saved filter information
 * @returns {function(*)}
 * @public
 */
const getFileAnalysisData = (data, format, callback) => {
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
        },
        onFailure: (response) => {
          const { meta: { message } } = response;
          callback.onFailure(message);
        }
      }
    });

    if (format === 'string') {
      dispatch({
        type: ACTION_TYPES.FETCH_FILE_ANALYZER_DATA,
        promise: api.getFileAnalysisStringFormatData(data)
      });
    } else {
      dispatch({
        type: ACTION_TYPES.FETCH_FILE_ANALYZER_DATA,
        promise: api.getFileAnalysisTextFormatData(data)
      });
    }
  };
};

const saveLocalFileCopy = (selectedFile, callback) => {
  return (dispatch, getState) => {
    api.saveLocalFileCopy(selectedFile.checksumSha256)
      .then(({ data }) => {
        if (data.id) {
          const { serverId } = getState().endpointQuery;
          const url = serverId ? `/rsa/endpoint/${serverId}/file/download?id=${data.id}&filename=${selectedFile.fileName}.zip` : '';
          dispatch({ type: ACTION_TYPES.SET_DOWNLOAD_FILE_LINK, payload: url });
        }
      })
      .catch((response) => {
        const { meta: { message } } = response;
        callback.onFailure(message);
      });
  };
};

export {
  getFileAnalysisData,
  toggleFileAnalysisView,
  saveLocalFileCopy
};
