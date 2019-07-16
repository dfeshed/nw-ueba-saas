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

// Need something like activeLanguageGroup, like we activeQueryGroup
// Something that doesn't change unless you hit queryEvents button.
export const defaultMetaGroup = createSelector(
  [_language],
  (language = []) => ({
    keys: language.reduce(_removeHiddenKeys, []).map(_createMetaGroup)
  })
);

/**
 * add three new boolean properties to meta
 * isIndexedByNone
 * isIndexedByValue
 * isIndexedByKey
 */
const _enrichedLanguage = createSelector(
  [_language],
  (language = []) => {
    return language.map((meta) => {
      const { flags = 1 } = meta;
      const index = (flags & '0xF') - 1;
      const indexedBy = indices[index];
      return {
        ...meta,
        isIndexedByNone: indexedBy === NONE,
        isIndexedByKey: indexedBy === KEY,
        isIndexedByValue: indexedBy === VALUE
      };
    });
  }
);

/**
 * Given a language array with meta suggestions for Guided query bar, the
 * function filters out:
 *   Meta key `time` - as we already have a time range
 * and defines `disabled` for meta dropdown based on the property `isIndexedByNone`
 *
 * @public
 */
export const metaKeySuggestionsForQueryBuilder = createSelector(
  _enrichedLanguage,
  (language = []) => {
    return language
      .filter((meta) => meta.metaName !== 'time')
      .map((meta) => ({
        ...meta,
        disabled: meta.isIndexedByNone && meta.metaName !== 'sessionid'
      }));
  }
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
export const validMetaKeySuggestions = createSelector(
  _enrichedLanguage,
  (language = []) => {
    return language
      .filter((m) => m.metaName !== 'time')
      .filter((meta) => {
        if (!meta.isIndexedByNone || meta.metaName === 'sessionid') {
          return true;
        }
        return false;
      });
  }
);
