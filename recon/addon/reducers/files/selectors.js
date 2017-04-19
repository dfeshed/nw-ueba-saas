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
    return (files || []).map((f) => {
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

/**
 * A selector that returns a sorted Array of all visible text.
 * @private
 */
export const selectedFiles = createSelector(
  [filesWithSelection],
  (filesWithSelection) => filesWithSelection.filterBy('selected', true)
);
