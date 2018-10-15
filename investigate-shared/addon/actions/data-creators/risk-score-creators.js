import * as ACTION_TYPES from 'investigate-shared/actions/types';
import api from 'investigate-shared/actions/api/risk-score/risk-score-api';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const resetRiskScore = (selectedFiles, callbacks = callbacksDefault) => {
  const fileList = selectedFiles.map((file) => file.checksumSha256);
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.RESET_RISK_SCORE,
      promise: api.sendDataToResetRiskScore(_prepareQuery(fileList)),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const _prepareQuery = (fileList) => {
  return {
    filter: [
      { field: 'hashes', value: fileList }
    ]
  };
};

export {
  resetRiskScore
};