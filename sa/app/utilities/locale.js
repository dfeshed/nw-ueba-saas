import _ from 'lodash';

export function normalizeLocales(locales, stateLocales) {
  const normalizedLocales = locales.map((locale) => {
    const localeString = locale.split('_');
    if (localeString.length === 2) {
      const [ label, id ] = localeString;
      return { id, label };
    }
  }).filter((locale) => {
    return locale !== undefined;
  });
  const mergedLocales = stateLocales.concat(normalizedLocales);
  return _.uniqWith(mergedLocales, _.isEqual);
}
