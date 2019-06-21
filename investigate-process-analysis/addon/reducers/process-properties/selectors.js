import reselect from 'reselect';
import config from './process-property-config';
import executionConfig from './proess-execution-config';

const { createSelector } = reselect;

const _processProperties = (state) => state.processAnalysis.processProperties.hostDetails || [];
const _osType = (state) => {
  const osType = state.processAnalysis.processTree.queryInput ? state.processAnalysis.processTree.queryInput.osType : 'windows';
  return osType;
};
const _rootHash = (state) => {
  const rootHash = state.processAnalysis.processTree.queryInput ? state.processAnalysis.processTree.queryInput.checksum : null;
  return rootHash;
};
const _propertyConfig = (state) => {
  const OSType = state.processAnalysis.processTree.queryInput ? state.processAnalysis.processTree.queryInput.osType : null;
  return OSType ? config(OSType) : [];
};

export const processExecutionConfig = () => {
  return executionConfig[0].fields;
};


export const processProperties = createSelector(
  _processProperties,
  (processProperties) => {
    return processProperties ? processProperties[0] : [];
  }
);

export const osType = createSelector(
  _osType,
  (osType) => osType
);

export const rootHash = createSelector(
  _rootHash,
  (rootHash) => rootHash
);

export const propertyConfig = createSelector(
  _propertyConfig,
  (propertyConfig) => propertyConfig
);

export const hasProperties = createSelector(
  _processProperties,
  (processProperties) => {
    if (processProperties && processProperties.length > 0) {
      if (processProperties[0].checksumSha256) {
        return true;
      }
    }
    return false;
  }
);

export const processDetails = (selectedProcess = {}) => {
  const isSourceMatching = selectedProcess.processVidSrc === selectedProcess.processId;

  const processDetails = {
    sessionId: selectedProcess.sessionId,
    processName: selectedProcess.processName,
    processId: selectedProcess.processId,
    eventTime: selectedProcess.eventTime,
    userAll: selectedProcess.userAll
  };

  if (isSourceMatching) {
    processDetails.paramDst = selectedProcess.paramSrc;
    processDetails.checksum = selectedProcess.checksumSrc;
    processDetails.directoryDst = selectedProcess.directorySrc;
  } else {
    processDetails.paramDst = selectedProcess.paramDst;
    processDetails.checksum = selectedProcess.checksumDst;
    processDetails.directoryDst = selectedProcess.directoryDst;
  }
  return processDetails;
};

