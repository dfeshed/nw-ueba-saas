import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import sort from 'fast-sort';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  metaGroups: null
});

export default handleActions({
  [ACTION_TYPES.META_GROUPS_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      failure: (s) => s.set('metaGroups', []),
      success: (s) => {
        const metaGroups = action.payload.data;
        if (metaGroups) {
          sort(metaGroups).by([{ asc: (group) => group.name.toUpperCase() }]);
          return s.set('metaGroups', metaGroups);
        } else {
          return s.set('metaGroups', []);
        }
      }
    });
  }
}, _initialState);
