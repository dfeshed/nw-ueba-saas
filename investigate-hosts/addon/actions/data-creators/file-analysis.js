import * as ACTION_TYPES from 'investigate-hosts/actions/types';

const toggleFileAnalysisView = (payload) => ({ type: ACTION_TYPES.TOGGLE_FILE_ANALYZER, payload });

const openAndFetchFileAnalyzerData = () => {
  return (dispatch) => {
    dispatch(toggleFileAnalysisView());
    // place holder for selected checksum to be passed to the api call.
  };
};

export {
  toggleFileAnalysisView,
  openAndFetchFileAnalyzerData
};
