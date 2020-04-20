import reselect from 'reselect';

const { createSelector } = reselect;
const files = (recon) => recon.files.files;
const selectedFileIds = (recon) => recon.files.selectedFileIds;

/**
 * A selector that returns a sorted Array of all visible text.
 * @private
 */
export const filesWithSelection = createSelector(
  [files, selectedFileIds],
  (files, selectedFileIds) => {
    if (!files) {
      return null;
    }

    return files.map((f) => {
      let selected = false;
      if (selectedFileIds.includes(f.id)) {
        // file is selected, unless it is a link file, can't select those
        selected = f.type !== 'link';
      }
      return {
        ...f,
        selected
      };
    });
  }
);

// There are no files if a files array is present
// and it is empty. If it isn't present, then we cannot
// say for certain if there is no files
export const hasNoFiles = createSelector(
  [files],
  (files) => files !== null && files.length === 0
);

// Files are retrieved if the list of files
// isn't null
export const filesRetrieved = createSelector(
  [files],
  (files) => files !== null
);

// Files are retrieved if the list of files
// isn't null
const sessionFiles = createSelector(
  [files],
  (files) => (files || []).filter((f) => f.type === 'session')
);

export const hasMultipleSessionFiles = createSelector(
  [sessionFiles],
  (files) => files.length > 1
);

/**
 * Returns an Array of all selected files.
 * @public
 */
export const selectedFiles = createSelector(
  [filesWithSelection],
  (filesWithSelection) => (filesWithSelection || []).filterBy('selected', true)
);

/**
 * Returns a Boolean representation of selectedFiles
 * @public
 */
export const hasSelectedFiles = createSelector(
  [selectedFiles],
  (selectedFiles) => !!selectedFiles.length
);
