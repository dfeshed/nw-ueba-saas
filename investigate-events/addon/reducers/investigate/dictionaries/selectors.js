import reselect from 'reselect';
import { isOpen, isHidden } from './utils';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _language = (state) => state.investigate.dictionaries.language;

// UTILS
const _removeHiddenKeys = (acc, obj) => {
  if (!isHidden(obj)) {
    acc.push(obj);
  }
  return acc;
};

const _createMetaGroup = (obj) => ({
  name: obj.metaName,
  isOpen: isOpen(obj)
});

// SELECTOR FUNCTIONS
export const defaultMetaGroup = createSelector(
  [_language],
  (language = []) => ({
    keys: language.reduce(_removeHiddenKeys, []).map(_createMetaGroup)
  })
);

// leaving out time as we already have panel to select time range for our
// queryBuilder
export const metaKeySuggestionsForQueryBuilder = createSelector(
  [_language],
  (language = []) => language.filter((d) => d.metaName !== 'time')
);
