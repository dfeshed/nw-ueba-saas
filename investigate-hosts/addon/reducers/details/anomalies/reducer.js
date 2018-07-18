import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import { fileContextHooksSchema } from './schemas';
import { normalize } from 'normalizr';

const initialState = Immutable.from({
  hooks: null,
  hooksLoadingStatus: null,
  selectedRowId: null
});

const anomalies = reduxActions.handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (s) => s.merge(initialState),

  [ACTION_TYPES.CHANGE_ANOMALIES_TAB]: (s) => s.set('selectedRowId', null),

  [ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG]: (s) => s.set('selectedRowId', null),

  [ACTION_TYPES.SET_ANOMALIES_SELECTED_ROW]: (state, { payload: { id } }) => state.set('selectedRowId', id),

  [ACTION_TYPES.FETCH_FILE_CONTEXT_HOOKS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('hooksLoadingStatus', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextHooksSchema);
        const { hooks } = normalizedData.entities;
        return s.merge({
          hooks,
          hooksLoadingStatus: 'completed',
          selectedRowId: null
        });
      }
    });
  }

}, initialState);

export default anomalies;

