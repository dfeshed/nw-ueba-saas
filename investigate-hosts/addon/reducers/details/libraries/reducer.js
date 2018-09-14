import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { fileContextListSchema } from './schemas';
import { normalize } from 'normalizr';
import Immutable from 'seamless-immutable';
import { getValues } from 'investigate-hosts/reducers/details/selector-utils';
import _ from 'lodash';

const initialState = Immutable.from({
  library: null,
  libraryLoadingStatus: null,
  selectedRowId: null,
  processList: null,
  selectedLibraryList: [],
  libraryStatusData: {}

});

const _toggleSelectedLibrary = (state, payload) => {
  const { selectedLibraryList } = state;
  const { id, checksumSha256, signature, size } = payload;
  let selectedList = [];
  // Previously selected driver

  if (selectedLibraryList.some((file) => file.id === id)) {
    selectedList = selectedLibraryList.filter((file) => file.id !== id);
  } else {
    selectedList = [...selectedLibraryList, { id, checksumSha256, signature, size }];
  }
  return state.merge({ 'selectedLibraryList': selectedList, 'libraryStatusData': {} });

};

const libraries = reduxActions.handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (s) => s.merge(initialState),

  [ACTION_TYPES.SET_DLLS_SELECTED_ROW]: (state, { payload: { id } }) => state.set('selectedRowId', id),

  [ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG]: (s) => s.set('selectedRowId', null),

  [ACTION_TYPES.GET_LIBRARY_PROCESS_INFO]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('processList', null),
      success: (s) => s.set('processList', action.payload.data)
    });
  },
  [ACTION_TYPES.FETCH_FILE_CONTEXT_DLLS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('libraryLoadingStatus', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data, fileContextListSchema);
        const { library } = normalizedData.entities;
        return s.merge({ library, libraryLoadingStatus: 'completed', selectedRowId: null });
      }
    });
  },
  [ACTION_TYPES.TOGGLE_SELECTED_LIBRARY]: (state, { payload }) => _toggleSelectedLibrary(state, payload),

  [ACTION_TYPES.TOGGLE_ALL_LIBRARY_SELECTION]: (state) => {
    const { library, selectedLibraryList } = state;
    const libraries = getValues(null, 'LIBRARIES', library, null);
    if (selectedLibraryList.length < libraries.length) {
      return state.set('selectedLibraryList', Object.values(libraries).map((library) => ({ id: library.id, checksumSha256: library.checksumSha256 })));
    } else {
      return state.set('selectedLibraryList', []);
    }
  },
  [ACTION_TYPES.SAVE_LIBRARY_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s, action) => {
        const library = _.clone(s.library);
        const libraries = _.values(library);
        const { payload: { request: { data } } } = action;
        const { checksums, fileStatus } = data;
        for (let i = 0; i < checksums.length; i++) {
          for (let j = 0; j < libraries.length; j++) {
            if (libraries[j].checksumSha256 == checksums[i]) {
              const fileProperties = { ...library[libraries[j].id].fileProperties, fileStatus };
              library[libraries[j].id] = { ...library[libraries[j].id], fileProperties };
            }
          }
        }
        return s.set('library', library);
      }
    });
  },
  [ACTION_TYPES.GET_LIBRARY_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [payLoadData] = action.payload.data;
        if (payLoadData && payLoadData.resultList.length) {
          return s.set('libraryStatusData', payLoadData.resultList[0].data);
        }
        return s;
      }
    });
  }

}, initialState);

export default libraries;

