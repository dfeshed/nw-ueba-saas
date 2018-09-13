import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/endpoint';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = Immutable.from({
  certificatesList: [],
  sortField: 'friendlyName',
  isSortDescending: true,
  pageNumber: 0,
  loadMoreStatus: 'stopped',
  hasMore: false,
  totalCertificates: 0,
  certificatesLoadingStatus: 'wait',
  selectedCertificateList: [],
  certificateStatusData: {}
});

const _handleAppendCertificates = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const certificatesData = data.items;
    const { certificatesList } = state;
    return state.merge({
      certificatesList: [...certificatesList, ...certificatesData],
      totalItems: data.totalItems,
      pageNumber: data.pageNumber,
      loadMoreStatus: data.hasNext || data.totalItems >= 1000 ? 'stopped' : 'completed',
      hasNext: data.hasNext
    });
  };
};

const _toggleSelectedCertificate = (state, payload) => {
  const { selectedCertificateList } = state;
  const { id, thumbprint } = payload;
  let selectedList = [];
  // Previously selected certificate

  if (selectedCertificateList.some((certificate) => certificate.id === id)) {
    selectedList = selectedCertificateList.filter((certificate) => certificate.id !== id);
  } else {
    selectedList = [...selectedCertificateList, { id, thumbprint }];
  }
  return state.merge({ 'selectedCertificateList': selectedList, 'certificateStatusData': {} });

};

const CertificateReducers = handleActions({

  [ACTION_TYPES.GET_CERTIFICATES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('loadMoreStatus', 'streaming'),
      failure: (s) => s.set('loadMoreStatus', 'error'),
      success: _handleAppendCertificates(action),
      finish: (s) => s.set('certificatesLoadingStatus', 'completed')
    });
  },

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1),

  [ACTION_TYPES.TOGGLE_SELECTED_CERTIFICATE]: (state, { payload }) => _toggleSelectedCertificate(state, payload),

  [ACTION_TYPES.TOGGLE_ALL_CERTIFICATE_SELECTION]: (state) => {
    const { certificatesList, selectedCertificateList } = state;
    if (selectedCertificateList.length < certificatesList.length) {
      return state.set('selectedCertificateList', Object.values(certificatesList).map((certificate) => ({ thumbprint: certificate.thumbprint })));
    } else {
      return state.set('selectedCertificateList', []);
    }
  }

}, initialState);

export default CertificateReducers;
