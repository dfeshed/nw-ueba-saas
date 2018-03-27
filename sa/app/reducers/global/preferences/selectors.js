import { createSelector } from 'reselect';

const camelize = (str) => {
  return str && str.replace(/^./g, function(firstChar) {
    return firstChar.toUpperCase() + str.slice(str.length);
  });
};

const normalizeLocale = (locale) => {
  return {
    ...locale,
    displayLabel: camelize(locale.label)
  };
};

const preferencesState = (state) => state.global.preferences;

export const getTheme = createSelector(
  preferencesState,
  (preferencesState) => preferencesState.theme
);

export const getLocale = createSelector(
  preferencesState,
  (preferencesState) => {
    return normalizeLocale(preferencesState.locale);
  }
);

export const getLocales = createSelector(
  preferencesState,
  (preferencesState) => {
    return preferencesState.locales.map((locale) => {
      return normalizeLocale(locale);
    });
  }
);
