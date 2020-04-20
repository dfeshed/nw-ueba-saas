import _ from 'lodash';

const format = (localeString) => {
  const [ language, national ] = localeString;
  const uppercaseNational = national.toUpperCase();
  return `${language}_${uppercaseNational}`;
};

const formatLocale = (key) => {
  const localeString = key.split('-');
  if (localeString.length === 2) {
    return format(localeString);
  }
};

const formatLocaleKey = (localeString, fileName) => {
  if (localeString && localeString.length === 2) {
    const [ label, key ] = localeString;
    const id = formatLocale(key);
    return id ? { id, key, label, fileName } : undefined;
  }
};

export function normalizeLocales(locales, stateLocales) {
  const normalizedLocales = locales.map((locale) => {
    const fileName = locale.name;
    const fileNameWithoutExt = fileName && fileName.replace(/\.[^/.]+$/, '');
    const localeString = fileNameWithoutExt && fileNameWithoutExt.split('_');
    return formatLocaleKey(localeString, fileName);
  }).filter((locale) => {
    return locale !== undefined;
  });
  const mergedLocales = stateLocales.concat(normalizedLocales);
  return _.uniqBy(mergedLocales, 'id');
}
