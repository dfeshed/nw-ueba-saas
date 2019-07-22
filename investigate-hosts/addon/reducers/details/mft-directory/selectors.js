
import reselect from 'reselect';
import _ from 'lodash';

const { createSelector } = reselect;
const _sortfield = (state) => state.endpoint.hostDownloads.mft.mftDirectory.sortField;
const _isSortDescending = (state) => state.endpoint.hostDownloads.mft.mftDirectory.isSortDescending;
const _mftFiles = (state) => state.endpoint.hostDownloads.mft.mftDirectory.files;
const _fileMftTotal = (state) => state.endpoint.hostDownloads.mft.mftDirectory.totalMftItems || 0;
const _areFilesLoading = (state) => state.endpoint.hostDownloads.mft.mftDirectory.loading;
const _hasMftNext = (state) => state.endpoint.hostDownloads.mft.mftDirectory.hasMftNext;
const _mftExpressionList = (state) => state.endpoint.hostDownloads.mft.filter.expressionList || [];
const _selectedMftFileList = (state) => state.endpoint.hostDownloads.mft.mftDirectory.selectedMftFileList || [];

export const areMftFilesLoading = createSelector(
  _areFilesLoading,
  (areFilesLoading) => {
    return areFilesLoading === 'wait';
  }
);
export const listOfMftFiles = createSelector(
  [ _mftFiles, _sortfield, _isSortDescending],
  (mftFiles, sortField, isSortDescending) => {
    let data = _.values(mftFiles);
    if (sortField) {
      data = data.sortBy(sortField);
      if (isSortDescending) {
        data.reverse();
      }
      return data;
    } else {
      // default sorting by creationTime.
      data = data.sortBy('creationTime');
      data.reverse();
      return data;
    }
  });

export const fileCount = createSelector(
  listOfMftFiles,
  (listOfMftFiles) => {
    return listOfMftFiles.length;
  }
);

export const hasMftFiles = createSelector(
  listOfMftFiles,
  (listOfMftFiles) => {
    return !!listOfMftFiles.length;
  }
);

export const isAllMftSelected = createSelector(
  [listOfMftFiles, _selectedMftFileList],
  (listOfMftFiles, selectedMftFileList) => {
    if (selectedMftFileList && selectedMftFileList.length) {
      return listOfMftFiles.length === selectedMftFileList.length;
    }
    return false;
  }
);
export const pageStatus = createSelector(
  [_hasMftNext, _areFilesLoading],
  (hasMftNext, loading) => {
    if (hasMftNext) {
      return loading === 'completed' ? 'stopped' : 'streaming';
    }
    return 'complete';
  }
);
export const mftSelectedFiles = createSelector(
  [listOfMftFiles, _selectedMftFileList],
  (listOfMftFiles, selectedMftFileList) => {
    if (selectedMftFileList && selectedMftFileList.length) {
      return selectedMftFileList.filter((file) => !file.directory);

    }
    return [];
  }
);

export const fileTotalLabel = createSelector(
  [_fileMftTotal, _mftExpressionList, _hasMftNext],
  (total, expressionList, hasNext) => {
    if (total >= 1000) {
      if (expressionList && expressionList.length && hasNext) {
        return '1000+';
      }
    }
    return `${total}`;
  }
);
export const nextLoadCount = createSelector(
  [listOfMftFiles],
  (listOfMftFiles) => {
    const ONE_PAGE_MAX_LENGTH = 100;
    return listOfMftFiles.length >= ONE_PAGE_MAX_LENGTH ? ONE_PAGE_MAX_LENGTH : listOfMftFiles.length;
  }
);