import * as ACTION_TYPES from 'configure/actions/types/endpoint';
import api from 'configure/actions/api/endpoint/certificates';
import { debug } from '@ember/debug';

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

const toggleCertificateSelection = (selectedCertificate) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_CERTIFICATE, payload: selectedCertificate });
const toggleAllCertificateSelection = () => ({ type: ACTION_TYPES.TOGGLE_ALL_CERTIFICATE_SELECTION });

export {
  getCertificates,
  getPageOfCertificates,
  toggleCertificateSelection,
  toggleAllCertificateSelection
};
