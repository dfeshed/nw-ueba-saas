import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { fileContextListSchema } from './schemas';
import { normalize } from 'normalizr';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  library: null,
  libraryLoadingStatus: null,
  selectedRowId: null,
  processList: null
});

const libraries = reduxActions.handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (s) => s.merge(initialState),

  [ACTION_TYPES.SET_DLLS_SELECTED_ROW]: (state, { payload: { id } }) => state.set('selectedRowId', id),

  [ACTION_TYPES.GET_LIBRARY_PROCESS_INFO]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('processList', null),
      success: (s) => s.set('processList', action.payload.data)
    });
  },
  [ACTION_TYPES.FETCH_FILE_CONTEXT_DLLS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('libraryLoadingStatus', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextListSchema);
        const { library } = normalizedData.entities;
        return s.merge({ library, libraryLoadingStatus: 'completed', selectedRowId: null });
      }
    });
  }

}, initialState);

export default libraries;

