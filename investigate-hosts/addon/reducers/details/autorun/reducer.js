import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { fileContextListSchema } from './schemas';
import { normalize } from 'normalizr';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  autorun: null,
  service: null,
  task: null,
  autorunLoadingStatus: null,
  selectedRowId: null
});

const autoruns = reduxActions.handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (s) => s.merge(initialState),

  [ACTION_TYPES.CHANGE_AUTORUNS_TAB]: (s) => s.set('selectedRowId', null),

  [ACTION_TYPES.SET_AUTORUN_SELECTED_ROW]: (state, { payload: { id } }) => state.set('selectedRowId', id),

  [ACTION_TYPES.FETCH_FILE_CONTEXT_AUTORUNS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('autorunLoadingStatus', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextListSchema);
        const { autorun, service, task } = normalizedData.entities;
        return s.merge({
          autorun,
          service,
          task,
          autorunLoadingStatus: 'completed',
          selectedRowId: null
        });
      }
    });
  }

}, initialState);

export default autoruns;

