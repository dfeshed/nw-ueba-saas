import reselect from 'reselect';
import config from './process-property-config';

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
export const processProperties = createSelector(
  _processProperties,
  (processProperties) => {
    return processProperties;
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

