import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-hosts/actions/types';

const fileAnalyzerState = Immutable.from({
  isFileAnalysisView: true,
  fileData: {
    format: 'pe'
  }
});

const fileAnalyzerReducer = handleActions({

  [ACTION_TYPES.TOGGLE_FILE_ANALYZER]: (state, { payload }) => {
    const isFileAnalysisView = (payload !== undefined) ? payload : !state.isFileAnalysisView;
    return state.set('isFileAnalysisView', isFileAnalysisView);
  },

  [ACTION_TYPES.FETCH_FILE_ANALYZER_DATA]: (state, { payload }) => { // placeholder for data returned by api.
    const { data } = payload;
    return state.set('fileData', data);
  }

}, fileAnalyzerState);

export default fileAnalyzerReducer;
