import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import _ from 'lodash';
import sort from 'fast-sort';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import EventColumnGroups from 'investigate-events/constants/OOTBColumnGroups';

const _initialState = Immutable.from({
  columnGroups: null,
  isColumnGroupsLoading: false,
  createColumnGroupErrorCode: undefined,
  createColumnGroupErrorMessage: undefined
});

/**
 * fix matching columns to widths
 *
 * @param {{ field, title, visible?, width? }[]} columnGroup columns in a column group
 * @param {{ field, width }[]} fieldsAndWidths array of { field, width }
 */
const _fixColumnWidth = (columnGroup, fieldsAndWidths) => {
  if (columnGroup) {
    fieldsAndWidths.forEach(({ field, width }) => {
      _.merge(_.find(columnGroup, { field }), { width });
    });
  }
};

export default handleActions({
  [ACTION_TYPES.COLUMNS_CREATE]: (state, action) => {
    return handle(state, action, {
      start: (s) => {
        return s.set({
          isColumnGroupsLoading: true,
          createColumnGroupErrorCode: null,
          createColumnGroupErrorMessage: null
        });
      },
      failure: (s) => s.merge({
        isColumnGroupsLoading: false,
        createColumnGroupErrorCode: action.payload.code,
        createColumnGroupErrorMessage: action.payload.meta.message
      }),
      success: (s) => {
        const createdColumnGroup = action.payload.data;
        // Want to fix certain sizes to certain columns
        // if those columns exist
        // meta-summary goes by a few names
        _fixColumnWidth(createdColumnGroup.columns, [
          { field: 'custom.meta-summary', width: 2000 },
          { field: 'custom.metasummary', width: 2000 },
          { field: 'time', width: 135 }
        ]);

        // add the newly created column group to state
        const columnGroups = s.columnGroups ?
          sort([...s.columnGroups, createdColumnGroup]).by([{ asc: (group) => group.name.toUpperCase() }]) :
          [createdColumnGroup];
        return s.merge({
          columnGroups,
          isColumnGroupsLoading: false
        });
      }
    });
  },

  [ACTION_TYPES.COLUMNS_RETRIEVE]: (state, action) => {
    sort(EventColumnGroups).by([{ asc: (group) => group.name.toUpperCase() }]);
    return handle(state, action, {
      failure: (s) => s.merge({ columnGroups: EventColumnGroups }),
      success: (s) => {
        const columnGroups = action.payload.data;
        if (columnGroups) {
          // Want to fix certain sizes to certain columns
          // if those columns exist
          columnGroups.forEach((cg) => {
            if (cg.columns) {
              // meta-summary goes by a few names
              _fixColumnWidth(cg.columns, [
                { field: 'custom.meta-summary', width: 2000 },
                { field: 'custom.metasummary', width: 2000 },
                { field: 'time', width: 135 }
              ]);
            }
          });

          sort(columnGroups).by([{ asc: (group) => group.name.toUpperCase() }]);
          return s.merge({ columnGroups });
        }

        // if none returned, return the default set of column groups
        return s.merge({ columnGroups: EventColumnGroups });
      }
    });
  }
}, _initialState);
