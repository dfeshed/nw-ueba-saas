import { createSelector } from 'reselect';
import { transform } from 'investigate-shared/utils/meta-util';

const _events = (state) => state.files.fileDetail.eventsData || [];
const _selectedFile = (state) => state.files.fileList.selectedDetailFile || {};

export const events = createSelector(
  _events,
  (events) => {
    return events.map(transform);
  }
);

export const fileSummary = createSelector(
  _selectedFile,
  (selectedFile) => {
    const { score, size, fileStatus, machineOsType, machineCount, signature, firstFileName } = selectedFile;
    const features = signature ? signature.features : [];
    return { score, size, fileStatus, machineOsType, machineCount, signature: features.join(), filename: firstFileName };
  }
);