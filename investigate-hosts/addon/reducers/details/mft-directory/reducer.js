import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import { fileListSchema } from './mft-file-schema';
import { normalize } from 'normalizr';
import _ from 'lodash';

const initialState = Immutable.from({
  files: {},
  subDirectories: [],
  loadMoreStatus: 'completed',
  selectedIndex: -1,
  totalItems: 4,
  sortField: 'creationTime',
  isSortDescending: true,
  selectedFileList: [],
  selectedFile: {},
  pageNumber: 0,
  selectedParentDirectory: { recordNumber: 0 },
  openDirectories: [],
  selectedDirectoryForDetails: 0,
  isDirectories: true,
  inUse: true,
  pageSize: 65000,
  fileSource: ''
});

const _addSubdirectoriesToParent = (directories, selectedParentDirectory, subDirectories, ancestors, recordNumber, subDirectoriesLevel) => {
  if (directories.recordNumber === selectedParentDirectory) {
    const subDirectoriesWithLevel = subDirectories.map((item) => ({ ...item, subDirectoriesLevel }));
    directories.children = [...subDirectoriesWithLevel];
    return directories;

  } else if (directories.children) {
    const { children } = directories;
    const selectedAncestor = children.find((item) => ancestors.includes(item.recordNumber) || (item.recordNumber === recordNumber));
    return _addSubdirectoriesToParent(selectedAncestor, selectedParentDirectory, subDirectories, ancestors, recordNumber, subDirectoriesLevel + 1);
  }
};


const _handleAppendFiles = (action) => {
  return (state) => {
    const { payload: { data } } = action;
    const { subDirectories, selectedParentDirectory: { recordNumber, ancestors } } = state;

    const { items } = data;
    let [ updatedDirectories ] = items;
    if (subDirectories.length) {
      const subDirectoriesLevel = 1;
      updatedDirectories = subDirectories[0].asMutable({ deep: true });
      _addSubdirectoriesToParent(updatedDirectories, recordNumber, items, ancestors, recordNumber, subDirectoriesLevel);
    }

    return state.merge({
      subDirectories: [updatedDirectories],
      pageNumber: data.pageNumber
    });
  };
};

const mftDirectory = reduxActions.handleActions({
  [ACTION_TYPES.RESET_MFT_FILE_DATA]: (state) => state.merge(initialState),

  [ACTION_TYPES.FETCH_MFT_SUBDIRECTORIES]: (state, action) => {
    return handle(state, action, {
      success: _handleAppendFiles(action)
    });
  },

  [ACTION_TYPES.FETCH_MFT_SUBDIRECTORIES_AND_FILES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('loading', 'wait'),
      success: (s) => {
        const normalizedData = normalize(action.payload.data.items, fileListSchema);
        const { files = {} } = normalizedData.entities;
        const totalItems = files ? _.values(files).length : 0;
        return s.merge({
          totalItems,
          files,
          loading: 'completed'
        });
      }
    });
  },

  [ACTION_TYPES.SET_SELECTED_MFT_PARENT_DIRECTORY]: (state, { payload }) => {
    const { selectedParentDirectory, pageSize, isDirectories, inUse } = payload;
    const { ancestors, recordNumber, close } = selectedParentDirectory;
    const { openDirectories } = state;
    const updatedAncestors = [...ancestors, recordNumber];
    let copyOfOpenDirectories = [...openDirectories];
    if (close) {
      copyOfOpenDirectories = copyOfOpenDirectories.filter((item) => item !== recordNumber);
    } else {
      updatedAncestors.forEach((item) => {
        if (!copyOfOpenDirectories.includes(item)) {
          copyOfOpenDirectories.push(item);
        }
      });
    }
    return state.merge({ selectedParentDirectory, openDirectories: copyOfOpenDirectories, pageSize, isDirectories, inUse });
  },

  [ACTION_TYPES.SET_SELECTED_MFT_DIRECTORY_FOR_DETAILS]: (state, { payload: { selectedDirectoryForDetails, fileSource, pageSize, isDirectories, inUse } }) => state.merge({
    selectedDirectoryForDetails,
    fileSource,
    pageSize,
    isDirectories,
    inUse
  }),

  [ACTION_TYPES.SET_FETCH_DIRECTORY_DETAILS]: (state, { payload: { pageSize, isDirectories, inUse } }) => state.merge({
    pageSize,
    isDirectories,
    inUse
  })

}, initialState);

export default mftDirectory;