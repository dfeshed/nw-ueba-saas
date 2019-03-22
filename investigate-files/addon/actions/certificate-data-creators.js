import * as ACTION_TYPES from './types';
import { Certificates } from './fetch';
import { debug } from '@ember/debug';
import { resetFilters } from 'investigate-shared/actions/data-creators/filter-creators';
import { run } from '@ember/runloop';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import { setupEndpointServer } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import _ from 'lodash';

const _expressionListForThumbprint = (thumbprint, selectedFileList) => {
  const certificateValues = selectedFileList.map((item) => {
    const { signature } = item;
    if (signature) {
      const { thumbprint } = signature;
      if (thumbprint) {
        return { value: thumbprint };
      }
    }
  }).compact();

  if (certificateValues.length) {
    return [{
      propertyName: 'thumbprint',
      propertyValues: _.uniqBy(certificateValues, 'value'),
      restrictionType: 'IN'
    }];
  } else if (thumbprint && thumbprint !== 'all') {
    return [{
      propertyName: 'thumbprint',
      propertyValues: [{ value: thumbprint }],
      restrictionType: 'IN'
    }];
  }
  return [];

};
/**
 * Bootstraping investigate Certificate page, loads all the endpoint server and checks for availability
 * @returns {Function}
 */
const bootstrapInvestigateCertificates = () => {
  return async(dispatch, getState) => {
    try {
      const { endpointServer: { serviceData } } = getState();
      // checking endpoint server availability
      if (!serviceData) {
        // Wait for endpoint server to load and availability
        await dispatch(setupEndpointServer());
      }
    } catch (e) {
      // Endpoint server offline
    }

  };
};
const initializeCertificateView = (thumbprint) => {

  return (dispatch, getState) => {

    const { files: { filter } } = getState();
    const { files: { fileList: { selectedFileList } } } = getState();
    //  To fix the filter reload issue we need to set the applied filter as a saved filter
    if (!filter.selectedFilter || filter.selectedFilter.id === 1) {
      const savedFilter = { id: 1, criteria: { expressionList: filter.expressionList } };
      dispatch({ type: SHARED_ACTION_TYPES.SET_SAVED_FILTER, payload: savedFilter, meta: { belongsTo: 'FILE' } });
    }
    dispatch({ type: SHARED_ACTION_TYPES.SET_DOWNLOAD_FILE_LINK, payload: null });
    dispatch({ type: ACTION_TYPES.TOGGLE_CERTIFICATE_VIEW });
    run.next(() => {
      // Allowing max 10 files selection to apply certificates filter.
      if (thumbprint || selectedFileList.length <= 10) {
        const expressionList = _expressionListForThumbprint(thumbprint, selectedFileList);
        const savedCertificateFilter = { id: 1, criteria: { expressionList } };
        dispatch({ type: SHARED_ACTION_TYPES.SET_SAVED_FILTER, payload: savedCertificateFilter, meta: { belongsTo: 'CERTIFICATE' } });
        dispatch({ type: SHARED_ACTION_TYPES.APPLY_FILTER, payload: expressionList, meta: { belongsTo: 'CERTIFICATE' } });
        dispatch(getFirstPageOfCertificates());
      } else {
        dispatch(resetFilters('CERTIFICATE'));
        dispatch(getFirstPageOfCertificates());
      }
    });
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
  initializeCertificateView,
  getCertificates,
  getPageOfCertificates,
  getFirstPageOfCertificates,
  getSavedCertificateStatus,
  saveCertificateStatus,
  toggleCertificateSelection,
  updateCertificateColumnVisibility,
  closeCertificateVIew,
  bootstrapInvestigateCertificates
};
