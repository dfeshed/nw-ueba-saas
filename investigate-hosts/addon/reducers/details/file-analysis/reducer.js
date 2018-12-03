import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';

const initialState = Immutable.from({
  isFileAnalysisView: false,
  fileData: null,
  filePropertiesData: null
});

const fileAnalyzerReducer = handleActions({

  [SHARED_ACTION_TYPES.TOGGLE_FILE_ANALYZER]: (state, { payload }) => {
    const isFileAnalysisView = (payload !== undefined) ? payload : !state.isFileAnalysisView;
    return isFileAnalysisView ? state.set('isFileAnalysisView', isFileAnalysisView) : state.merge(initialState);
  },

  [SHARED_ACTION_TYPES.FETCH_FILE_ANALYZER_DATA]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        return s.set('fileData', action.payload.data);
      }
    });
  },

  [SHARED_ACTION_TYPES.FETCH_FILE_ANALYZER_FILE_PROPERTIES_DATA]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        return s.set('filePropertiesData', action.payload.data);
      }
    });
  }

}, initialState);

export default fileAnalyzerReducer;
