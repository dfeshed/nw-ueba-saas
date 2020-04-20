
const hasRestrictedEntry = (fileList = [], restrictedFileList = []) => {
  return fileList.some((fileName) => restrictedFileList.indexOf(fileName) >= 0);
};

const isAllAreRestrictedEntry = (fileList = [], restrictedFileList = []) => {
  return fileList.every((fileName) => restrictedFileList.indexOf(fileName) >= 0);
};

const checksumsWithoutRestricted = (fileList, restrictedFileList) => {
  const filteredList = fileList.filter((file) => restrictedFileList.indexOf(file.fileName) < 0);
  return filteredList.mapBy('checksumSha256');
};

export {
  checksumsWithoutRestricted,
  hasRestrictedEntry,
  isAllAreRestrictedEntry
};
