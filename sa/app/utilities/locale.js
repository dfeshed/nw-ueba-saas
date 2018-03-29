import _ from 'lodash';

export function normalizeLocales(locales, stateLocales) {
  const normalizedLocales = locales.map((fileName) => {
    const localeString = fileName.split('_');
    if (localeString.length === 2) {
      const [ label, id ] = localeString;
      return { id, label, fileName };
    }
  }).filter((locale) => {
    return locale !== undefined;
  });
  const mergedLocales = stateLocales.concat(normalizedLocales);
  return _.uniqBy(mergedLocales, 'id');
}
