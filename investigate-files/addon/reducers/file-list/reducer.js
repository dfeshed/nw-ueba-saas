import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const fileListState = Immutable.from({
  files: [],
  areFilesLoading: 'wait',
  loadMoreStatus: 'stopped',
  pageNumber: -1,
  totalItems: 0,
  hasNext: false,
  sortField: 'firstSeenTime',
  isSortDescending: false,
  downloadStatus: 'completed',
  downloadId: null
});

const _handleAppendFiles = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { files } = state;

    return state.merge({
      files: [...files, ...data.items],
      totalItems: data.totalItems,
      pageNumber: data.pageNumber,
      loadMoreStatus: data.hasNext ? 'stopped' : 'completed',
      hasNext: data.hasNext
    });
  };
};

const fileListReducer = handleActions({
  [ACTION_TYPES.FETCH_INIT_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ files: [], areFilesLoading: 'wait' }),
      failure: (s) => s.set('areFilesLoading', 'error'),
      success: (s) => s.merge({
        files: action.payload.data.items,
        totalItems: action.payload.data.totalItems,
        pageNumber: action.payload.data.pageNumber,
        areFilesLoading: 'completed'
      })
    });
  },

  [ACTION_TYPES.FETCH_NEXT_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('loadMoreStatus', 'streaming'),
      finish: (s) => s.set('areFilesLoading', 'completed'),
      failure: (s) => s.merge({
        areFilesLoading: 'error',
        loadMoreStatus: 'error'
      }),
      success: _handleAppendFiles(action)
    });
  },

  [ACTION_TYPES.DOWNLOAD_FILE_AS_CSV]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('downloadStatus', 'streaming'),
      success: (s) => s.merge({
        downloadId: action.payload.data.id,
        downloadStatus: 'completed'
      })
    });
  },

  [ACTION_TYPES.SET_SORT_BY]: (state, { payload: { sortField, isSortDescending } }) => state.merge({
    sortField,
    areFilesLoading: 'sorting',
    isSortDescending
  }),

  [ACTION_TYPES.RESET_FILES]: (state) => state.merge({
    files: [],
    pageNumber: -1
  }),

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1),

  [ACTION_TYPES.RESET_DOWNLOAD_ID]: (state) => state.set('downloadId', null)
}, fileListState);

export default fileListReducer;
