import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import {
  fileContextAutorunsSchema,
  fileContextServicesSchema,
  fileContextTasksSchema
} from './schemas';
import { normalize } from 'normalizr';
import Immutable from 'seamless-immutable';
import { getValues } from 'investigate-hosts/reducers/details/selector-utils';

const initialState = Immutable.from({
  autorun: null,
  service: null,
  task: null,
  autorunLoadingStatus: null,
  serviceLoadingStatus: null,
  taskLoadingStatus: null,
  selectedRowId: null,
  selectedAutorunList: [],
  autorunStatusData: {},
  selectedServiceList: [],
  serviceStatusData: {},
  selectedTaskList: [],
  taskStatusData: {}
});

const _toggleSelectedAutorun = (state, payload) => {
  const { selectedAutorunList } = state;
  const { id, checksumSha256, signature, size } = payload;
  let selectedList = [];
  // Previously selected file

  if (selectedAutorunList.some((file) => file.id === id)) {
    selectedList = selectedAutorunList.filter((file) => file.id !== id);
  } else {
    selectedList = [...selectedAutorunList, { id, checksumSha256, signature, size }];
  }
  return state.merge({ 'selectedAutorunList': selectedList, 'autorunStatusData': {} });

};

const _toggleSelectedService = (state, payload) => {
  const { selectedServiceList } = state;
  const { id, checksumSha256, signature, size } = payload;
  let selectedList = [];
  // Previously selected file

  if (selectedServiceList.some((file) => file.id === id)) {
    selectedList = selectedServiceList.filter((file) => file.id !== id);
  } else {
    selectedList = [...selectedServiceList, { id, checksumSha256, signature, size }];
  }
  return state.merge({ 'selectedServiceList': selectedList, 'serviceStatusData': {} });

};

const _toggleSelectedTask = (state, payload) => {
  const { selectedTaskList } = state;
  const { id, checksumSha256, signature, size } = payload;
  let selectedList = [];
  // Previously selected file

  if (selectedTaskList.some((file) => file.id === id)) {
    selectedList = selectedTaskList.filter((file) => file.id !== id);
  } else {
    selectedList = [...selectedTaskList, { id, checksumSha256, signature, size }];
  }
  return state.merge({ 'selectedTaskList': selectedList, 'taskStatusData': {} });

};
const autoruns = reduxActions.handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (s) => s.merge(initialState),

  [ACTION_TYPES.CHANGE_AUTORUNS_TAB]: (s) => s.set('selectedRowId', null),

  [ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG]: (s) => s.set('selectedRowId', null),

  [ACTION_TYPES.SET_AUTORUN_SELECTED_ROW]: (state, { payload: { id } }) => state.set('selectedRowId', id),

  [ACTION_TYPES.FETCH_FILE_CONTEXT_AUTORUNS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('autorunLoadingStatus', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextAutorunsSchema);
        const { autorun } = normalizedData.entities;
        return s.merge({
          autorun,
          autorunLoadingStatus: 'completed',
          selectedRowId: null
        });
      }
    });
  },

  [ACTION_TYPES.FETCH_FILE_CONTEXT_SERVICES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('serviceLoadingStatus', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextServicesSchema);
        const { service } = normalizedData.entities;
        return s.merge({
          service,
          serviceLoadingStatus: 'completed',
          selectedRowId: null
        });
      }
    });
  },

  [ACTION_TYPES.FETCH_FILE_CONTEXT_TASKS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('taskLoadingStatus', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextTasksSchema);
        const { task } = normalizedData.entities;
        return s.merge({
          task,
          taskLoadingStatus: 'completed',
          selectedRowId: null
        });
      }
    });
  },
  [ACTION_TYPES.TOGGLE_SELECTED_AUTORUN]: (state, { payload }) => _toggleSelectedAutorun(state, payload),

  [ACTION_TYPES.TOGGLE_ALL_AUTORUN_SELECTION]: (state) => {
    const { autorun, selectedAutorunList } = state;
    const autoruns = getValues(null, 'AUTORUNS', autorun, null);
    if (selectedAutorunList.length < autoruns.length) {
      return state.set('selectedAutorunList', Object.values(autoruns).map((autorun) => ({ id: autorun.id, checksumSha256: autorun.checksumSha256 })));
    } else {
      return state.set('selectedAutorunList', []);
    }
  },
  [ACTION_TYPES.GET_AUTORUN_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [payLoadData] = action.payload.data;
        if (payLoadData && payLoadData.resultList.length) {
          return s.set('autorunStatusData', payLoadData.resultList[0].data);
        }
        return s;
      }
    });
  },
  [ACTION_TYPES.TOGGLE_SELECTED_SERVICE]: (state, { payload }) => _toggleSelectedService(state, payload),

  [ACTION_TYPES.TOGGLE_ALL_SERVICE_SELECTION]: (state) => {
    const { service, selectedServiceList } = state;
    const services = getValues(null, 'SERVICES', service, null);
    if (selectedServiceList.length < autoruns.length) {
      return state.set('selectedServiceList', Object.values(services).map((service) => ({ id: service.id, checksumSha256: service.checksumSha256 })));
    } else {
      return state.set('selectedServiceList', []);
    }
  },
  [ACTION_TYPES.GET_SERVICE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [payLoadData] = action.payload.data;
        if (payLoadData && payLoadData.resultList.length) {
          return s.set('serviceStatusData', payLoadData.resultList[0].data);
        }
        return s;
      }
    });
  },
  [ACTION_TYPES.TOGGLE_SELECTED_TASK]: (state, { payload }) => _toggleSelectedTask(state, payload),

  [ACTION_TYPES.TOGGLE_ALL_TASK_SELECTION]: (state) => {
    const { task, selectedTaskList } = state;
    const tasks = getValues(null, 'TASKS', task, null);
    if (selectedTaskList.length < tasks.length) {
      return state.set('selectedTaskList', Object.values(tasks).map((task) => ({ id: task.id, checksumSha256: task.checksumSha256 })));
    } else {
      return state.set('selectedTaskList', []);
    }
  },
  [ACTION_TYPES.GET_TASK_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [payLoadData] = action.payload.data;
        if (payLoadData && payLoadData.resultList.length) {
          return s.set('taskStatusData', payLoadData.resultList[0].data);
        }
        return s;
      }
    });
  }

}, initialState);

export default autoruns;

