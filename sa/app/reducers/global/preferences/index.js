import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'sa/actions/types';
import { normalizeLocales } from 'sa/utilities/locale';

export const DEFAULT_THEME = 'DARK';
export const DEFAULT_LOCALE = { id: 'en_US', label: 'english' };
export const DEFAULT_LOCALES = [DEFAULT_LOCALE];

const initialState = {
  theme: DEFAULT_THEME,
  locale: DEFAULT_LOCALE,
  locales: DEFAULT_LOCALES
};

export default function preferences(state, action) {
  switch (action.type) {
    case ACTION_TYPES.REHYDRATE: {
      const { payload } = action;
      if (payload && payload.global && payload.global.preferences) {
        const { preferences } = payload.global;
        const theme = preferences && preferences.theme;
        const locale = preferences && preferences.locale;
        return state.merge({
          theme: theme || state.theme,
          locale: locale || state.locale
        });
      }
      return state;
    }
    case ACTION_TYPES.UPDATE_PREFERENCES_THEME: {
      if (action && action.theme && action.theme !== 'null' && action.theme !== 'undefined') {
        return state.merge({
          theme: action.theme
        });
      }
      return state;
    }
    case ACTION_TYPES.ADD_PREFERENCES_LOCALES: {
      const { locales } = action;
      if (locales) {
        const normalizedLocales = normalizeLocales(locales, state.locales);
        return state.merge({
          locales: normalizedLocales
        });
      }
      return state;
    }
    case ACTION_TYPES.UPDATE_PREFERENCES_LOCALE: {
      if (action && action.locale && action.locale !== 'null' && action.locale !== 'undefined') {
        return state.merge({
          locale: action.locale
        });
      }
      return state;
    }

    default: {
      return state || Immutable.from(initialState);
    }
  }
}
