import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'sa/actions/types';
import { normalizeLocales } from 'sa/utilities/locale';

export const DEFAULT_THEME = 'DARK';
export const DEFAULT_LOCALES = [{ 'english': 'en' }];

const initialState = {
  theme: DEFAULT_THEME,
  locales: DEFAULT_LOCALES
};

export default function preferences(state, action) {
  switch (action.type) {
    case ACTION_TYPES.REHYDRATE: {
      const { payload } = action;
      if (payload && payload.global && payload.global.preferences && payload.global.preferences.theme) {
        return state.merge({
          theme: payload.global.preferences.theme
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

    default: {
      return state || Immutable.from(initialState);
    }
  }
}
