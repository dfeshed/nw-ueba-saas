import reselect from 'reselect';
import languageUtil from 'investigate-events/state/helpers/language-utils';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
// These tell the selectors below _where_ to find the property in state.
// These functions are passed within the first Array argument to
// `createSelector()`
const _language = (state) => state.dictionaries.language;

const _removeHiddenKeys = (acc, obj) => {
  if (!languageUtil.isHidden(obj)) {
    acc.push(obj);
  }
  return acc;
};

const _createMetaGroup = (obj) => ({
  name: obj.metaName,
  isOpen: languageUtil.isOpen(obj)
});

// SELECTOR FUNCTIONS
export const defaultMetaGroup = createSelector(
  [_language],
  (language) => ({
    keys: language.reduce(_removeHiddenKeys, []).map(_createMetaGroup)
  })
);
