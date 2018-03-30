import * as ACTION_TYPES from '../types';
import { lookup } from 'ember-dependency-lookup';
import { getLocale, getLocales } from 'sa/reducers/global/preferences/selectors';

export function updateTheme(theme) {
  const request = lookup('service:request');
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme });
    return request.promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          themeType: theme
        }
      }
    }).catch(() => {
      const translationService = lookup('service:i18n');
      const errorMessage = translationService.t('userPreferences.theme.error');
      lookup('service:flashMessages').error(errorMessage);
    });
  };
}

export function updateLocale(locale) {
  const request = lookup('service:request');
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale });
    return request.promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          userLocale: locale.id
        }
      }
    }).catch(() => {
      const translationService = lookup('service:i18n');
      const errorMessage = translationService.t('userPreferences.locale.error');
      lookup('service:flashMessages').error(errorMessage);
    });
  };
}

export function updateLocaleByKey(userLocale) {
  return (dispatch, getState) => {
    const state = getState();
    const locale = getLocale(state);
    if (locale && locale.id !== userLocale) {
      const locales = getLocales(state);
      const localeFound = locales && locales.filter((loc) => loc.id === userLocale);
      if (localeFound && localeFound.length === 1) {
        dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_LOCALE, locale: localeFound[0] });
      } else {
        const translationService = lookup('service:i18n');
        const errorMessage = translationService.t('userPreferences.locale.fetchError');
        lookup('service:flashMessages').error(errorMessage);
      }
    }
  };
}
