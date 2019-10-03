import reselect from 'reselect';
const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
export const profiles = (state) => state.investigate.profile.profiles || undefined;

// UTIL
const _profileWithIsEditable = (item) => ({
  ...item,
  isEditable: item.contentType && item.contentType !== 'OOTB'
});

// SELECTORS
export const profilesWithIsEditable = createSelector(
  [profiles],
  (profiles = []) => {
    return profiles.map(_profileWithIsEditable);
  }
);
