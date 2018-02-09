import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const hostFilesState = Immutable.from({
  files: [],
  selectedFileId: null,
  pageNumber: -1,
  totalItems: 0,
  sortField: 'fileName',
  isDescOrder: false,
  filesLoadingStatus: 'wait',
  filesLoadMoreStatus: 'stopped'
});

const _handleAppendFiles = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { files = [] } = state;
    const allFiles = [...files, ...data.items];
    return state.merge({
      totalItems: data.totalItems,
      files: allFiles,
      pageNumber: data.pageNumber,
      filesLoadMoreStatus: data.hasNext ? 'stopped' : 'completed'
    });
  };
};

const hostFilesReducer = handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (state) => state.merge(hostFilesState),

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1),

  [ACTION_TYPES.RESET_HOST_FILES]: (state) => state.merge({ pageNumber: -1, files: [] }),

  [ACTION_TYPES.SET_SELECTED_FILE]: (state, { payload: { id } }) => state.set('selectedFileId', id),

  [ACTION_TYPES.GET_HOST_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('filesLoadMoreStatus', 'streaming'),
      finish: (s) => s.set('filesLoadingStatus', 'completed'),
      failure: (s) => s.merge({ filesLoadingStatus: 'error', filesLoadMoreStatus: 'error' }),
      success: _handleAppendFiles(action)
    });
  },

  [ACTION_TYPES.SET_HOST_FILES_SORT_BY]: (state, { payload: { sortOption: { sortField, isDescOrder } } }) => {
    return state.merge({
      sortField,
      filesLoadingStatus: 'sorting',
      isDescOrder
    });
  }

}, hostFilesState);

export default hostFilesReducer;
