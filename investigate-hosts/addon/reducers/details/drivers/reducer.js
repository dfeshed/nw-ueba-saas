import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { fileContextListSchema } from './schemas';
import { normalize } from 'normalizr';
import Immutable from 'seamless-immutable';
import { getValues } from 'investigate-hosts/reducers/details/selector-utils';


const initialState = Immutable.from({
  driver: null,
  driverLoadingStatus: null,
  selectedRowId: null,
  selectedDriverList: [],
  driverStatusData: {}
});

const _toggleSelectedDriver = (state, payload) => {
  const { selectedDriverList } = state;
  const { id, checksumSha256, signature, size } = payload;
  let selectedList = [];
  // Previously selected driver

  if (selectedDriverList.some((file) => file.id === id)) {
    selectedList = selectedDriverList.filter((file) => file.id !== id);
  } else {
    selectedList = [...selectedDriverList, { id, checksumSha256, signature, size }];
  }
  return state.merge({ 'selectedDriverList': selectedList, 'driverStatusData': {} });

};

const drivers = reduxActions.handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (state) => state.merge(initialState),

  [ACTION_TYPES.SET_DRIVERS_SELECTED_ROW]: (state, { payload: { id } }) => state.set('selectedRowId', id),

  [ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG]: (s) => s.set('selectedRowId', null),

  [ACTION_TYPES.FETCH_FILE_CONTEXT_DRIVERS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('driverLoadingStatus', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextListSchema);
        const { driver } = normalizedData.entities;
        return s.merge({ driver, driverLoadingStatus: 'completed', selectedRowId: null });
      }
    });
  },

  [ACTION_TYPES.TOGGLE_SELECTED_DRIVER]: (state, { payload }) => _toggleSelectedDriver(state, payload),

  [ACTION_TYPES.TOGGLE_ALL_DRIVER_SELECTION]: (state) => {
    const { driver, selectedDriverList } = state;
    const drivers = getValues(null, 'DRIVERS', driver, null);
    if (selectedDriverList.length < drivers.length) {
      return state.set('selectedDriverList', Object.values(drivers).map((driver) => ({ id: driver.id, checksumSha256: driver.checksumSha256 })));
    } else {
      return state.set('selectedDriverList', []);
    }
  },
  [ACTION_TYPES.GET_DRIVER_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [payLoadData] = action.payload.data;
        if (payLoadData && payLoadData.resultList.length) {
          return s.set('driverStatusData', payLoadData.resultList[0].data);
        }
        return s;
      }
    });
  }

}, initialState);

export default drivers;