import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import sort from 'fast-sort';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import EventColumnGroups from 'investigate-events/constants/OOTBColumnGroups';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';

const _initialState = Immutable.from({
  columnGroups: null
});

export default handleActions({
  [ACTION_TYPES.COLUMNS_RETRIEVE]: (state, action) => {
    const mappedColumnGroups = mapColumnGroupsForEventTable(EventColumnGroups);

    sort(EventColumnGroups).by([{ asc: (group) => group.name.toUpperCase() }]);
    return handle(state, action, {
      failure: (s) => s.merge({ columnGroups: mappedColumnGroups }),
      success: (s) => {

        const columnGroups = mapColumnGroupsForEventTable(action.payload.data);

        if (columnGroups) {

          sort(columnGroups).by([{ asc: (group) => group.name.toUpperCase() }]);
          return s.merge({ columnGroups });
        }

        // if none returned, return the default set of column groups
        return s.set({ columnGroups: mappedColumnGroups });
      }
    });
  }
}, _initialState);
