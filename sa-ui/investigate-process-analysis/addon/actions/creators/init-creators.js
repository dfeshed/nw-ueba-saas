import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';

export const initProcessAnalysis = (input) => ({
  type: ACTION_TYPES.SET_PROCESS_ANALYSIS_INPUT,
  payload: input
});
