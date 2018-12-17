import * as ACTION_TYPES from './types';
import { Certificates } from './fetch';
import { debug } from '@ember/debug';
import { resetFilters } from 'investigate-shared/actions/data-creators/filter-creators';
import { run } from '@ember/runloop';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';

const toggleCertificateView = () => {
  return (dispatch, getState) => {
    const { files: { filter } } = getState();
    //  To fix the filter reload issue we need to set the applied filter as a saved filter
    if (!filter.selectedFilter || filter.selectedFilter.id === -1) {
      const savedFilter = { id: -1, criteria: { expressionList: filter.expressionList } };
      dispatch({ type: SHARED_ACTION_TYPES.SET_SAVED_FILTER, payload: savedFilter, meta: { belongsTo: 'FILE' } });
    }
    dispatch({ type: ACTION_TYPES.TOGGLE_CERTIFICATE_VIEW });
    const { isCertificateView } = getState().certificate.list;
    if (isCertificateView) {
      run.next(() => {
        dispatch(resetFilters('CERTIFICATE'));
      });
    }
  };
};
const callbacksDefault = { onSuccess() {}, onFailure() {} };

const getCertificates = () => {
  return (dispatch, getState) => {
    const { sortField, isSortDescending, pageNumber } = getState().certificate.list;
    const { expressionList } = getState().certificate.filter;
    dispatch({
      type: ACTION_TYPES.GET_CERTIFICATES,
      promise: Certificates.getCertificates(pageNumber, { sortField, isSortDescending }, expressionList),
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
 * Action Creator to retrieve the paged certificates. Increments the current page number and updates the state.
 * @return {function} redux-thunk
 * @public
 */
const getPageOfCertificates = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.INCREMENT_CERTIFICATE_PAGE_NUMBER });
    dispatch(getCertificates());
  };
};

/**
 * Action Creator for fetching the first page of data. Before sending the request resets the state
 * @returns {function(*)}
 * @private
 */
const getFirstPageOfCertificates = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.RESET_CERTIFICATES });
    dispatch(getCertificates());
  };
};

const saveCertificateStatus = (thumbprints, data, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SAVE_CERTIFICATE_STATUS,
      promise: Certificates.setCertificateStatus({ ...data, thumbprints }),
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
  promise: Certificates.getCertificateStatus(selections)
});

const toggleCertificateSelection = (selectedCertificate) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_CERTIFICATE, payload: selectedCertificate });

const updateCertificateColumnVisibility = (column) => ({ type: ACTION_TYPES.UPDATE_CERTIFICATE_COLUMN_VISIBILITY, payload: column });

const closeCertificateVIew = () => ({ type: ACTION_TYPES.CLOSE_CERTIFICATE_VIEW });

export {
  toggleCertificateView,
  getCertificates,
  getPageOfCertificates,
  getFirstPageOfCertificates,
  getSavedCertificateStatus,
  saveCertificateStatus,
  toggleCertificateSelection,
  updateCertificateColumnVisibility,
  closeCertificateVIew
};