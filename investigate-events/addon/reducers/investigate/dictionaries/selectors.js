import reselect from 'reselect';
import { isOpen } from './utils';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

const NONE = 'none';
const KEY = 'key';
const VALUE = 'value';
const indices = [NONE, KEY, VALUE];

// ACCESSOR FUNCTIONS
const _language = (state) => state.investigate.dictionaries.language;
const _aliases = (state) => state.investigate.dictionaries.aliases;

// UTILS

const _createMetaGroup = (obj) => ({
  name: obj.metaName,
  isOpen: isOpen(obj),
  disabled: obj.isIndexedByNone && obj.metaName !== 'sessionid'
});

// SELECTOR FUNCTIONS

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

export const defaultMetaGroupEnriched = createSelector(
  _enrichedLanguage,
  (language = []) => {
    return { keys: language.map(_createMetaGroup) };
  }
);

/**
 * Given a language array with meta suggestions for Guided query bar, the
 * function filters out:
 *   Meta key `time` - as we already have a time range
 * and defines `disabled` for meta dropdown based on the property `isIndexedByNone`
 * and defines icon related properties based on indexed by key, value, none
 *
 * @public
 */
export const metaKeySuggestionsForQueryBuilder = createSelector(
  _enrichedLanguage,
  (language = []) => {
    const i18n = lookup('service:i18n');
    return language
      .filter((meta) => meta.metaName !== 'time')
      .map((meta) => {
        // set values for icon to display
        // based on indexed by none, value, key
        const iconProperties = { iconClass: null, iconTitle: null, iconName: null };
        if (meta.isIndexedByValue) {
          iconProperties.iconClass = 'is-indexed-by-value value-index-indicator';
          iconProperties.iconName = 'search';
          iconProperties.iconTitle = i18n.t('queryBuilder.indexedByValue').toString();
        } else if (meta.isIndexedByKey) {
          iconProperties.iconClass = 'is-indexed-by-key key-index-indicator';
          iconProperties.iconName = 'login-key';
          iconProperties.iconTitle = i18n.t('queryBuilder.indexedByKey').toString();
        } else if (meta.isIndexedByNone) {
          iconProperties.iconClass = 'is-indexed-by-none none-index-indicator';
          iconProperties.iconName = 'search-remove';
          iconProperties.iconTitle = i18n.t('queryBuilder.indexedByNone').toString();
        }

        return {
          ...meta,
          disabled: meta.isIndexedByNone && meta.metaName !== 'sessionid',
          ...iconProperties
        };
      });
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

/**
 * Returns the language dictionary with only time filtered out, along with
 * the aliases dictionary.
 *
 * @public
 */
export const languageAndAliasesForParser = createSelector(
  [ _enrichedLanguage, _aliases ],
  (language = [], aliases = []) => ({
    language: language.filter((m) => m.metaName !== 'time'),
    aliases
  })
);
