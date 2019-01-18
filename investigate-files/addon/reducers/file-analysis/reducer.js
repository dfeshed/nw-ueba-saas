import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import * as ACTION_TYPES from 'investigate-files/actions/types';

const initialState = Immutable.from({
  fileData: null,
  filePropertiesData: null,
  fileDataLoadingStatus: null
});

const fileAnalyzerReducer = handleActions({

  [SHARED_ACTION_TYPES.FETCH_FILE_ANALYZER_DATA]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('fileDataLoadingStatus', 'loading'),
      failure: (s) => {
        return s.merge({ 'fileData': null, 'fileDataLoadingStatus': null });
      },
      success: (s) => {
        return s.merge({ 'fileData': action.payload.data, 'fileDataLoadingStatus': 'completed' });
      }
    });
  },

  [SHARED_ACTION_TYPES.FETCH_FILE_ANALYZER_FILE_PROPERTIES_DATA]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        return s.set('filePropertiesData', action.payload.data);
      }
    });
  },

  [ACTION_TYPES.RESET_INPUT_DATA]: (state) => state.merge(initialState)

}, initialState);

export default fileAnalyzerReducer;