import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'sa/actions/types';

const initialState = {
  theme: 'DARK'
};

export default function preferences(state, action) {
  switch (action.type) {
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
