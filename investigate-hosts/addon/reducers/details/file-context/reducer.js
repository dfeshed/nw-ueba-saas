import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { getSchema } from './schemas';
import { normalize } from 'normalizr';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  fileContext: {},
  contextLoadingStatus: null,
  contextLoadMoreStatus: null,
  selectedRowId: null,
  fileContextSelections: [],
  sortConfig: null,
  fileStatus: {},
  totalItems: null,
  pageNumber: -1,
  hasNext: false
});


const _handleAppendFiles = (action) => {
  return (state) => {
    const { payload: { data }, meta: { name } } = action;
    const { fileContext } = state;
    const normalizedData = normalize(action.payload.data.items, [getSchema(name)]);
    const newData = normalizedData.entities[name];
    return state.merge({
      fileContext: { ...fileContext, ...newData },
      totalItems: data.totalItems,
      pageNumber: data.pageNumber,
      contextLoadMoreStatus: data.hasNext ? 'stopped' : 'completed',
      hasNext: data.hasNext
    });
  };
};

const _toggleSelection = (state, payload) => {
  const { fileContextSelections } = state;
  const { id, checksumSha256, signature, size } = payload;
  let selectedList = [];
  // Previously selected driver

  if (fileContextSelections.some((file) => file.id === id)) {
    selectedList = fileContextSelections.filter((file) => file.id !== id);
  } else {
    selectedList = [...fileContextSelections, { id, checksumSha256, signature, size }];
  }
  return state.merge({ 'fileContextSelections': selectedList, 'fileStatus': {} });

};

const fileContext = reduxActions.handleActions({

  [ACTION_TYPES.RESET_CONTEXT_DATA]: (state) => state.merge(initialState),

  [ACTION_TYPES.SET_FILE_CONTEXT_ROW_SELECTION]: (state, { payload: { id } }) => state.set('selectedRowId', id),

  [ACTION_TYPES.SET_FILE_CONTEXT_COLUMN_SORT]: (s, { payload }) => {
    const { isDescending, field } = payload;
    return s.merge({ sortConfig: { isDescending, field }, selectedRowId: null });
  },

  [ACTION_TYPES.FETCH_FILE_CONTEXT]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('contextLoadingStatus', 'wait'),
      success: (s) => {
        const contextType = action.meta.name;
        const normalizedData = normalize(action.payload.data, getSchema(contextType));
        const fileContext = normalizedData.entities[contextType];
        return s.merge({
          fileContext,
          contextLoadingStatus: 'completed',
          selectedRowId: null
        });
      }
    });
  },

  [ACTION_TYPES.TOGGLE_FILE_CONTEXT_ROW_SELECTION]: (state, { payload }) => _toggleSelection(state, payload),

  [ACTION_TYPES.TOGGLE_FILE_CONTEXT_ALL_SELECTION]: (state) => {
    const { fileContext, fileContextSelections } = state;
    const contexts = Object.values(fileContext);
    if (fileContextSelections.length < contexts.length) {
      return state.set('fileContextSelections', contexts.map((driver) => ({
        id: driver.id,
        checksumSha256: driver.checksumSha256
      })));
    } else {
      return state.set('fileContextSelections', []);
    }
  },

  [ACTION_TYPES.SAVE_FILE_CONTEXT_FILE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s, action) => {
        const { fileContextSelections, fileContext } = s;
        const { payload: { request: { data } } } = action;
        const { fileStatus } = data;
        for (let i = 0; i < fileContextSelections.length; i++) {
          const { id } = fileContextSelections[i];
          const obj = fileContext[id];
          const newData = obj.setIn(['fileProperties', 'fileStatus'], fileStatus);
          s = s.setIn(['fileContext', `${id}`], newData);
        }
        return s;
      }
    });
  },

  [ACTION_TYPES.GET_FILE_CONTEXT_FILE_STATUS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        const [payLoadData] = action.payload.data;
        if (payLoadData && payLoadData.resultList.length) {
          return s.set('fileStatus', payLoadData.resultList[0].data);
        }
        return s;
      }
    });
  },

  [ACTION_TYPES.FETCH_FILE_CONTEXT_PAGINATED]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ 'contextLoadingStatus': 'streaming', contextLoadMoreStatus: 'streaming' }),
      finish: (s) => s.set('contextLoadingStatus', 'completed'),
      failure: (s) => s.merge({ contextLoadingStatus: 'error' }),
      success: _handleAppendFiles(action)
    });
  },

  [ACTION_TYPES.INCREMENT_PAGE_NUMBER]: (state) => state.set('pageNumber', state.pageNumber + 1)

}, initialState);

export default fileContext;
