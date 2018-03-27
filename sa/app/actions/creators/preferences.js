import * as ACTION_TYPES from '../types';
import { lookup } from 'ember-dependency-lookup';

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
