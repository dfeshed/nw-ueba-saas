import { createSelector } from 'reselect';
import { componentSelectionForFileType } from 'investigate-shared/utils/file-analysis-view-util';

const _fileType = (state) => {
  const { filePropertiesData } = state.endpoint.fileAnalysis;
  return filePropertiesData ? filePropertiesData.format : 'pe';
};
export const componentConfig = createSelector(
  [_fileType],
  (fileType) => {
    return componentSelectionForFileType(fileType);
  }
);