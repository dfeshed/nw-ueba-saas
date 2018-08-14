import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import {
  fileContextImageHooksSchema,
  fileContextThreadsSchema,
  fileContextKernelHooksSchema
} from './schemas';
import { normalize } from 'normalizr';


const initialState = Immutable.from({
  imageHooks: null,
  threads: null,
  kernelHooks: null,
  imageHooksLoadingStatus: null,
  threadsLoadingStatus: null,
  kernelHooksLoadingStatus: null,
  selectedRowId: null
});

const anomalies = reduxActions.handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (s) => s.merge(initialState),

  [ACTION_TYPES.CHANGE_ANOMALIES_TAB]: (s) => s.set('selectedRowId', null),

  [ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG]: (s) => s.set('selectedRowId', null),

  [ACTION_TYPES.SET_ANOMALIES_SELECTED_ROW]: (state, { payload: { id } }) => state.set('selectedRowId', id),

  [ACTION_TYPES.FETCH_FILE_CONTEXT_IMAGE_HOOKS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ imageHooksLoadingStatus: 'wait', imageHooks: {} }),
      failure: (s) => s.merge({ imageHooksLoadingStatus: 'rejected' }),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextImageHooksSchema);
        const { imageHooks = {} } = normalizedData.entities;

        return s.merge({
          imageHooks,
          imageHooksLoadingStatus: 'completed',
          selectedRowId: null
        });
      }
    });
  },

  [ACTION_TYPES.FETCH_FILE_CONTEXT_THREADS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ threadsLoadingStatus: 'wait', threads: {} }),
      failure: (s) => s.merge({ threadsLoadingStatus: 'rejected' }),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextThreadsSchema);
        const { threads = {} } = normalizedData.entities;

        return s.merge({
          threads,
          threadsLoadingStatus: 'completed',
          selectedRowId: null
        });
      }
    });
  },

  [ACTION_TYPES.FETCH_FILE_CONTEXT_KERNEL_HOOKS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ kernelHooksLoadingStatus: 'wait', kernelHooks: {} }),
      failure: (s) => s.merge({ kernelHooksLoadingStatus: 'rejected' }),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextKernelHooksSchema);
        const { kernelHooks } = normalizedData.entities;

        return s.merge({
          kernelHooks,
          kernelHooksLoadingStatus: 'completed',
          selectedRowId: null
        });
      }
    });
  }

}, initialState);

export default anomalies;

