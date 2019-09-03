import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/file-analysis/file-analysis-api';
import { lookup } from 'ember-dependency-lookup';

const NOOP = () => ({});
const toggleFileAnalysisView = (payload) => ({ type: ACTION_TYPES.TOGGLE_FILE_ANALYZER, payload });
/**
 * An action creator for getting the saved filter information
 * @returns {function(*)}
 * @public
 */
const getFileAnalysisData = (data, format, socketUrlPostfix, callback = { onSuccess: NOOP, onFailure: NOOP }) => {
  return (dispatch) => {
    const request = lookup('service:request');
    request.registerPersistentStreamOptions({ socketUrlPostfix, requiredSocketUrl: 'endpoint/socket' });

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
          const { meta } = response;
          if (meta) {
            callback.onFailure(meta.message);
          }
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
  return (dispatch) => {
    const { downloadInfo, id, serviceId } = selectedFile;
    const { refId, serviceId: sourceSid } = downloadInfo ? downloadInfo : { refId: id, serviceId };

    api.saveLocalFileCopy(refId, sourceSid)
      .then(({ data }) => {
        if (data.id) {
          const url = sourceSid ? `/rsa/endpoint/${sourceSid}/file/download?id=${data.id}&filename=${selectedFile.fileName}.zip` : '';
          dispatch({ type: ACTION_TYPES.SET_DOWNLOAD_FILE_LINK, payload: url });
        }
      })
      .catch((response) => {
        const { meta } = response;
        if (meta) {
          callback.onFailure(meta.message);
        }
      });
  };
};

export {
  getFileAnalysisData,
  toggleFileAnalysisView,
  saveLocalFileCopy
};
