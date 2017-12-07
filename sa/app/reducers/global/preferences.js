import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'sa/actions/types';

export const DEFAULT_THEME = 'DARK';

const initialState = {
  theme: DEFAULT_THEME
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

    default: {
      return state || Immutable.from(initialState);
    }
  }
}
