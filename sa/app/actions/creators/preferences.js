import * as ACTION_TYPES from '../types';
import { promiseRequest } from 'streaming-data/services/data-access/requests';
import { lookup } from 'ember-dependency-lookup';

export function updateTheme(theme) {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme });
    return promiseRequest({
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
