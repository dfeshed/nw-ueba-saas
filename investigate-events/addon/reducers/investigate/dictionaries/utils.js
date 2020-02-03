import _ from 'lodash';
import sort from 'fast-sort';

// Mask for the index level of a language's meta key.
export const LANGUAGE_KEY_INDEX_MASK = 0x000F;

// Indicates that key is filtered, so data related to the key should be ignored.
export const LANGUAGE_KEY_INDEX_FILTER = 0;

// Indicates that key is indexed at the key and value level.
export const LANGUAGE_KEY_INDEX_VALUES = 3;

// Mask for default behavior of language's meta key in Investigate UI.
export const LANGUAGE_KEY_ACTION_MASK = 0x0F00;

// Default behavior is to hide the key.
export const LANGUAGE_KEY_ACTION_HIDDEN = 0x0100;

// Default behavior is to show the key & its values.
export const LANGUAGE_KEY_ACTION_OPEN = 0x0200;

// Default behavior is to show the key closed, without its values.
export const LANGUAGE_KEY_ACTION_CLOSE = 0x0300;

// Default behavior is to auto open the key, based on its index level.
export const LANGUAGE_KEY_ACTION_AUTO = 0x0400;

// Special flags about each language key
export const LANGUAGE_KEY_SPECIAL_MASK = 0x000000F0;

// Token appears only once per session
export const LANGUAGE_KEY_SPECIAL_SINGLETON = 0x00000010;

// returns formattedName for language object
const _getFormattedName = (item) => {
  return item.metaName ? `${item.metaName} (${item.displayName})` : item.displayName;
};

// Some keys are always hidden from view by UI.
const LANGUAGE_KEYS_ALWAYS_HIDDEN = ['time', 'sessionid'];
const LANGUAGE_KEYS_ALWAYS_HIDDEN_HASH = {};
LANGUAGE_KEYS_ALWAYS_HIDDEN.forEach((name) => {
  LANGUAGE_KEYS_ALWAYS_HIDDEN_HASH[name] = true;
});

// Reads the action setting from a given meta key definition. The action setting
// is read from a bit in the meta key definition's `flags` property.
const _actionFlag = (languageKey) => {
  const action = languageKey.flags & LANGUAGE_KEY_ACTION_MASK;
  return (
    action !== LANGUAGE_KEY_ACTION_HIDDEN &&
    action !== LANGUAGE_KEY_ACTION_OPEN &&
    action !== LANGUAGE_KEY_ACTION_CLOSE &&
    action !== LANGUAGE_KEY_ACTION_AUTO
  ) ? LANGUAGE_KEY_ACTION_HIDDEN : action;
};

// Reads the index level from a given meta key definition. The index level is
// read from a bit in the meta key definition's `flags` property.
const _indexLevel = (languageKey) => languageKey.flags & LANGUAGE_KEY_INDEX_MASK;

/**
 * Given a given meta key's definition, determines whether that key should be
 * hidden from the Meta navigation UI. Certain keys are always hidden
 * (e.g., `time` & `sessionid`). Additionally, meta key definitions include a
 * setting which specifies whether the key should be hidden, closed or opened by
 * default. That setting (called the default "action" of the meta key) is read
 * from a bit in the definition's `flags` property. The key should be hidden if
 * either:
 * (a) the "action" setting resolves to LANGUAGE_KEY_ACTION_HIDDEN; or
 * (b) the "action" setting resolves to LANGUAGE_KEY_ACTION_AUTO and the
 * "index level" of the meta key is not sufficient.
 * @param {object} languageKey The meta key definition object.
 * @returns {boolean}
 * @public
 */
export const isHidden = (languageKey) => {
  if (LANGUAGE_KEYS_ALWAYS_HIDDEN_HASH[languageKey.metaName]) {
    return true;
  }
  const action = _actionFlag(languageKey);
  return action === LANGUAGE_KEY_ACTION_HIDDEN || (
    action === LANGUAGE_KEY_ACTION_AUTO &&
    _indexLevel(languageKey) === LANGUAGE_KEY_INDEX_FILTER
  );
};

/**
 * Given a meta key's definition, determines whther that key should be opened by
 * default in the Meta navigation UI. The meta key definition includes a setting
 * which specifies whether the key should be opened, closed or auto by default.
 * That setting is read from a bit in the definition's `flags` property. They
 * should be opened by default if:
 * (a) the setting resolves to LANGUAGE_KEY_ACTION_OPEN; or
 * (b) the setting resolves to LANGUAGE_KEY_ACTION_AUTO *and* the meta key's
 * values are indexed.
 * @param {object} languageKey The meta key definition object.
 * @returns {boolean}
 * @public
 */
export const isOpen = (languageKey) => {
  const action = _actionFlag(languageKey);
  let ret = false;
  if (action === LANGUAGE_KEY_ACTION_OPEN) {
    ret = true;
  } else if (action === LANGUAGE_KEY_ACTION_AUTO) {
    ret = _indexLevel(languageKey) === LANGUAGE_KEY_INDEX_VALUES;
  }
  return ret;
};

/**
 * iterates over data array to construct aliases
 * returns aliases
 *
 * @param {object[]} data { language: { }, aliases: { } }[]
 *
 */
export const getAliases = (data) => {
  const aliases = {};
  if (data && data.length) {
    data.forEach((item) => {
      if (!_.isEmpty(item.aliases)) {
        aliases[item.language.metaName] = _.cloneDeep(item.aliases);
      }
    });
  }
  return aliases;
};

/**
 * iterates over data array to get language
 * returns language
 *
 * @param {object[]} data { language: { }, aliases: { } }[]
 *
 */
export const getLanguage = (data) => {
  if (data && data.length) {
    const enrichedLanguage = data.map((item) => {
      const languageItem = _.cloneDeep(item.language);
      languageItem.formattedName = _getFormattedName(item.language);
      return languageItem;
    });
    sort(enrichedLanguage).by([{ asc: (item) => item.metaName.toUpperCase() }]);
    return enrichedLanguage;
  }
  return [];
};
