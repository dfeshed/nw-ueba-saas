import { createSelector } from 'reselect';
const _selectedFile = (state) => state.files.fileList.selectedDetailFile || {};

export const fileSummary = createSelector(
  _selectedFile,
  (selectedFile) => {
    const { score, size, fileStatus, machineOsType, machineCount, signature, hostCount, firstFileName } = selectedFile;
    const features = signature ? signature.features : [];
    return { score, size, fileStatus, machineOsType, machineCount, signature: features.join(), hostCount, filename: firstFileName };
  }
);