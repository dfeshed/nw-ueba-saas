import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { fileContextListSchema } from './schemas';
import { normalize } from 'normalizr';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  driver: null,
  driverLoadingStatus: null,
  selectedRowId: null
});

const drivers = reduxActions.handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (state) => state.merge(initialState),

  [ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG]: (state) => state.set('selectedRowId', null),

  [ACTION_TYPES.SET_DRIVERS_SELECTED_ROW]: (state, { payload: { id } }) => state.set('selectedRowId', id),

  [ACTION_TYPES.FETCH_FILE_CONTEXT_DRIVERS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('driverLoadingStatus', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextListSchema);
        const { driver } = normalizedData.entities;
        return s.merge({ driver, driverLoadingStatus: 'completed', selectedRowId: null });
      }
    });
  }

}, initialState);

export default drivers;

