import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'sa/actions/types';

const initialState = {
  theme: 'DARK'
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
      return state.merge({
        theme: action.theme
      });
    }

    default: {
      return state || Immutable.from(initialState);
    }
  }
}
