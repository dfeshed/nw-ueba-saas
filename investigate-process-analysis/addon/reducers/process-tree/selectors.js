import reselect from 'reselect';

const { createSelector } = reselect;

const _rootNode = (state) => state.processAnalysis.processTree.rootNode;
const _streaming = (state) => state.processAnalysis.processTree.streaming;
const _error = (state) => state.processAnalysis.processTree.error;

export const treeData = createSelector(
  [_rootNode],
  (rootNode) => {
    return rootNode;
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
