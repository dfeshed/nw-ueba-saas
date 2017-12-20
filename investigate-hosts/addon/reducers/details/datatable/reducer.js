import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import Immutable from 'seamless-immutable';

const dataInitialState = Immutable.from({
  // Host details datatable input
  sortConfig: {
    autoruns: null,
    services: null,
    tasks: null,
    libraries: null,
    drivers: null
  }
});

const data = handleActions({
  [ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG]: (state, { payload }) => {
    const { tabName, isDescending, field } = payload;
    const tab = tabName.toLowerCase();
    return state.setIn(['sortConfig', tab], { isDescending, field });
  }
}, dataInitialState);

export default data;
