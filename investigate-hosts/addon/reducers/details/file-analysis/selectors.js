import { createSelector } from 'reselect';
import { componentSelectionForfileType } from 'investigate-shared/utils/file-analysis-view-util';

export const _fileType = (state) => state.endpoint.fileAnalysis.fileData.format;

export const componentConfig = createSelector(
  [_fileType],
  (fileType) => {
    return componentSelectionForfileType(fileType);
  }
);