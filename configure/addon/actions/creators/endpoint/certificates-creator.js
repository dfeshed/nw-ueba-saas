import * as ACTION_TYPES from 'configure/actions/types/endpoint';
import api from 'configure/actions/api/endpoint/certificates';
import { debug } from '@ember/debug';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const getCertificates = () => {
  return (dispatch, getState) => {
    const { sortField, isSortDescending, pageNumber } = getState().configure.endpoint.certificates;
    const expressionList = [];
    dispatch({
      type: ACTION_TYPES.GET_CERTIFICATES,
      promise: api.getCertificates(pageNumber, { sortField, isSortDescending }, expressionList),
      meta: {
        onSuccess: (response) => {
          const debugResponse = JSON.stringify(response);
          debug(`onSuccess: ${ACTION_TYPES.GET_CERTIFICATES} ${debugResponse}`);
        }
      }
    });
  };
};

/**
 * Action Creator to retrieve the paged files. Increments the current page number and updates the state.
 * @return {function} redux-thunk
 * @public
 */
const getPageOfCertificates = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.INCREMENT_PAGE_NUMBER });
    dispatch(getCertificates());
  };
};

const saveCertificateStatus = (thumbprints, data, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SAVE_CERTIFICATE_STATUS,
      promise: api.setCertificateStatus({ ...data, thumbprints }),
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

const getSavedCertificateStatus = (selections) => ({
  type: ACTION_TYPES.GET_CERTIFICATE_STATUS,
  promise: api.getCertificateStatus(selections)
});

const toggleCertificateSelection = (selectedCertificate) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_CERTIFICATE, payload: selectedCertificate });
const toggleAllCertificateSelection = () => ({ type: ACTION_TYPES.TOGGLE_ALL_CERTIFICATE_SELECTION });

export {
  getCertificates,
  getPageOfCertificates,
  getSavedCertificateStatus,
  saveCertificateStatus,
  toggleCertificateSelection,
  toggleAllCertificateSelection
};
