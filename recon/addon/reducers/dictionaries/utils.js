import _ from 'lodash';

// returns formattedName for language object
const _getFormattedName = (item) => {
  return item.metaName ? `${item.metaName} (${item.displayName})` : item.displayName;
};

const _sortByMetaName = (a, b) => {
  const nameA = a.metaName.toUpperCase();
  const nameB = b.metaName.toUpperCase();
  if (nameA < nameB) {
    return -1;
  }
  if (nameA > nameB) {
    return 1;
  }
  return 0;
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
    return data.map((item) => {
      const languageItem = _.cloneDeep(item.language);
      languageItem.formattedName = _getFormattedName(item.language);
      return languageItem;
    }).sort(_sortByMetaName);
  }
  return [];
};
