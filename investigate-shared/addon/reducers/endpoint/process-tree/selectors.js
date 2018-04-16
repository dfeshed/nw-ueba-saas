import reselect from 'reselect';

const { createSelector } = reselect;

const _rootNode = (state) => state.investigateShared.endpoint.processTree.rootNode;
const _streaming = (state) => state.investigateShared.endpoint.processTree.streaming;
const _error = (state) => state.investigateShared.endpoint.processTree.error;

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
