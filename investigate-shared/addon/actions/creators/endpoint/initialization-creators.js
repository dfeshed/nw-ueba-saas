import * as ACTION_TYPES from 'investigate-shared/actions/types/endpoint';

export const setProcessAnalysisInput = (input) => ({
  type: ACTION_TYPES.SET_PROCESS_ANALYSIS_INPUT,
  payload: input
});
