import reselect from 'reselect';
import { isOpen, isHidden } from './utils';

const { createSelector } = reselect;

const NONE = 'none';
const KEY = 'key';
const VALUE = 'value';
const indices = [NONE, KEY, VALUE];

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

/**
 * Given a language array with meta suggestions for Guided query bar, the
 * function filters out:
 * 1) Meta key `time` - as we already have a time range
 * 2) All meta keys which are indexed none. Exception to this rule is
 * sessionid, which we'd want to show.
 *
 * @public
 */
export const metaKeySuggestionsForQueryBuilder = createSelector(
  [_language],
  (language = []) => {
    return language
      .filter((m) => m.metaName !== 'time')
      .filter((meta) => {
        const { flags = 1 } = meta;
        const index = (flags & '0xF') - 1;
        const indexedBy = indices[index];
        if (indexedBy !== NONE || meta.metaName === 'sessionid') {
          return true;
        }
        return false;
      });
  }
);