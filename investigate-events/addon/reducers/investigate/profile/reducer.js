import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import sort from 'fast-sort';
import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  profiles: undefined
});

export default handleActions({
  [ACTION_TYPES.PROFILES_RETRIEVE]: (state, action) => {
    const profilesData = action.payload ?
      sort(action.payload.data).by([{ asc: (group) => group.name.toUpperCase() }]) : [];

    return handle(state, action, {
      failure: (s) => s.set('profiles', []),
      success: (s) => {
        if (profilesData) {
          // profiles retrieved
          return s.set('profiles', profilesData);
        } else {
          // if no profiles returned
          return s.set('profiles', []);
        }
      }
    });
  }
}, _initialState);
