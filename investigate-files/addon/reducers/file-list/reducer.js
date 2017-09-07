import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const fileListState = {
  files: [],
  areFilesLoading: 'wait',
  loadMoreStatus: 'stopped',
  pageNumber: -1,
  totalItems: 0,
  hasNext: false,
  sortField: 'firstName',
  isSortDescending: false
};

const _handleAppendFiles = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { files } = state;
    const allFiles = [...files, ...data.items];
    return {
      ...state,
      totalItems: data.totalItems,
      files: allFiles,
      pageNumber: data.pageNumber,
      loadMoreStatus: data.hasNext ? 'stopped' : 'completed',
      hasNext: data.hasNext
    };
  };
};

const fileListReducer = handleActions({

  [ACTION_TYPES.FETCH_INIT_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, files: [] }),
      failure: (s) => ({ ...s, areFilesLoading: 'error' }),
      success: (s) => ({
        ...s,
        files: action.payload.data.items,
        totalItems: action.payload.data.totalItems,
        pageNumber: action.payload.data.pageNumber,
        areFilesLoading: 'completed'
      })
    });
  },

  [ACTION_TYPES.FETCH_NEXT_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, loadMoreStatus: 'streaming' }),
      finish: (s) => ({ ...s, areFilesLoading: 'completed' }),
      failure: (s) => ({ ...s, areFilesLoading: 'error', loadMoreStatus: 'error' }),
      success: _handleAppendFiles(action)
    });
  },

  [ACTION_TYPES.SET_SORT_BY]: (state, { payload: { sortField, isSortDescending } }) => ({
    ...state,
    sortField,
    areFilesLoading: 'sorting',
    isSortDescending
  }),

  [ACTION_TYPES.RESET_FILES]: (state) => ({
    ...state,
    files: [],
    pageNumber: -1
  }),

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => ({
    ...state,
    pageNumber: state.pageNumber + 1
  })

}, fileListState);

export default fileListReducer;
