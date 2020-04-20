import reselect from 'reselect';
import { isOpen } from './utils';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

const NONE = 'none';
const KEY = 'key';
const VALUE = 'value';
const indices = [NONE, KEY, VALUE];

// ACCESSOR FUNCTIONS
const _aliases = (state) => state.investigate.dictionaries.aliases;
const _aliasesCache = (state) => state.investigate.dictionaries.aliasesCache;
const _currentServiceId = (state) => state.investigate.queryNode.previousQueryParams?.serviceId;
const _language = (state) => state.investigate.dictionaries.language;
const _languageCache = (state) => state.investigate.dictionaries.languageCache;
const _metaKeyCache = (state) => state.investigate.dictionaries.metaKeyCache;

// UTILS

const _createMetaGroup = (obj) => ({
  name: obj.metaName,
  isOpen: isOpen(obj),
  disabled: obj.isIndexedByNone && obj.metaName !== 'sessionid'
});

// SELECTOR FUNCTIONS

/**
 * Provides the language for the service that's currently being used by a query.
 * Takes the serviceId from the queryNode's previousQueryParams object, and
 * uses that to look for the language in the cache. If it does not find cached
 * language, it falls back to the language associated with the currently
 * selected service. Warning, this could provide a different language set
 * because you can change the selected service without actually executing the
 * query.
 */
export const currentQueryLanguage = createSelector(
  [_currentServiceId, _language, _languageCache],
  (currentServiceId, language, languageCache) => {
    let currentQueryLanguage;
    if (currentServiceId) {
      currentQueryLanguage = languageCache[currentServiceId];
    }
    return currentQueryLanguage ? currentQueryLanguage : language;
  }
);

/**
 * Provides the aliases for the service that's currently being used by a query.
 * Takes the serviceId from the queryNode's previousQueryParams object, and
 * uses that to look for the aliases in the cache. If it does not find cached
 * aliases, it falls back to the aliases associated with the currently
 * selected service. Warning, this could provide a different aliases set
 * because you can change the selected service without actually executing the
 * query.
 */
export const currentQueryAliases = createSelector(
  [_currentServiceId, _aliases, _aliasesCache],
  (currentServiceId, aliases, aliasesCache) => {
    let currentQueryAliases;
    if (currentServiceId) {
      currentQueryAliases = aliasesCache[currentServiceId];
    }
    return currentQueryAliases ? currentQueryAliases : aliases;
  }
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
        if (meta.metaName === 'sessionid') {
          iconProperties.iconClass = 'is-sessionid sessionid-indicator';
          iconProperties.iconName = 'login-key';
          iconProperties.iconTitle = i18n.t('queryBuilder.sessionid').toString();
        } else if (meta.isIndexedByValue) {
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

/**
 * Returns the metaKeys from metaKeyCache as an array of candidate meta
 * for column groups
 *
 * @public
 */
export const metaMapForColumns = createSelector(
  [ _metaKeyCache],
  (metaKeys = []) => {

    const mappedKeys = metaKeys.map((meta) => {
      return {
        field: meta.metaName,
        title: meta.displayName
      };
    });

    // meta keys time and medium need not be shown to the user for creation of meta/column groups
    // since they are added by default
    return mappedKeys.filter((meta) => meta.field !== 'time' && meta.field !== 'medium');
  }
);
