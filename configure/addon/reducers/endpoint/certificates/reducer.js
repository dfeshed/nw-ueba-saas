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
  certificateStatusData: {},
  statusData: {}
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
  const { thumbprint } = payload;

  return state.merge({ 'selectedCertificateList': [{ thumbprint }], 'certificateStatusData': {} });

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

  [ACTION_TYPES.SAVE_CERTIFICATE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        let list = s.certificatesList;
        const { payload: { request: { data } } } = action;
        const { thumbprints, certificateStatus } = data;
        for (let i = 0; i < thumbprints.length; i++) {
          const index = list.findIndex((item) => item.thumbprint === thumbprints[i]);
          list = list.setIn([index, 'certificateStatus'], certificateStatus);
        }
        return s.set('certificatesList', list);
      }
    });
  },

  [ACTION_TYPES.GET_CERTIFICATE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [payLoadData] = action.payload.data;
        if (payLoadData && payLoadData.resultList.length) {
          return s.set('statusData', payLoadData.resultList[0].data);
        }
        return s;
      }
    });
  },

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1),

  [ACTION_TYPES.TOGGLE_SELECTED_CERTIFICATE]: (state, { payload }) => _toggleSelectedCertificate(state, payload),

  [ACTION_TYPES.RESET_CERTIFICATES]: (state) => state.merge(initialState)

}, initialState);

export default CertificateReducers;
