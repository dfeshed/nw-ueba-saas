import reselect from 'reselect';

const { createSelector } = reselect;

const _streaming = (state) => state.processAnalysis.processTree.streaming;
const _error = (state) => state.processAnalysis.processTree.error;
const _queryInput = (state) => state.processAnalysis.processTree.queryInput;
const _rawData = (state) => state.processAnalysis.processTree.rawData;

export const queryInput = createSelector(
  [_queryInput],
  (queryInput) => {
    return queryInput;
  }
);

export const isStreaming = createSelector(
  [_streaming],
  (streaming) => {
    return streaming;
  }
);

export const hasError = createSelector(
  [_error],
  (error) => {
    return error !== null;
  }
);

export const errorMessage = createSelector(
  [_error],
  (error) => {
    return error;
  }
);

export const children = createSelector(
  [_rawData],
  (rawData) => {
    if (rawData) {
      return rawData.asMutable({ deep: true });
    }
    return [];
  }
);

export const rootProcess = createSelector(
  [queryInput],
  (queryInput) => {
    if (queryInput) {
      return {
        processName: queryInput.pn,
        checksum: queryInput.checksum,
        agentId: queryInput.aid
      };
    }
    return null;
  }
);
