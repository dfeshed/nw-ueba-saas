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
    const requiresTransform = !['en_US', 'es_MX'].includes(locale.id);
    const userLocale = requiresTransform ? locale.langCode : locale.id;
    return request.promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          userLocale
        }
      }
    }).catch(() => {
      const translationService = lookup('service:i18n');
      const errorMessage = translationService.t('userPreferences.locale.error');
      lookup('service:flashMessages').error(errorMessage);
    });
  };
}

export function updateLocaleByKey(localeString) {
  return (dispatch, getState) => {
    const state = getState();
    const locale = getLocale(state);
    const userLocale = localeString && localeString.replace(/_(.*)/, '');
    if (userLocale && locale && locale.id !== userLocale) {
      const locales = getLocales(state);
      const localeFound = locales && locales.filter((loc) => loc.langCode === userLocale);
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
