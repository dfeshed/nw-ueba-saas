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
  certificatesLoadingStatus: 'wait'
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

const CertificateReducers = handleActions({

  [ACTION_TYPES.GET_CERTIFICATES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('loadMoreStatus', 'streaming'),
      failure: (s) => s.set('loadMoreStatus', 'error'),
      success: _handleAppendCertificates(action),
      finish: (s) => s.set('certificatesLoadingStatus', 'completed')
    });
  },

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1)

}, initialState);

export default CertificateReducers;
