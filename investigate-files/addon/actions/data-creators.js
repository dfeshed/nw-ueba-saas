/**
 * @file Investigate Files Data Action Creators
 * Action creators for data retrieval,
 * or for actions that have data side effects
 *
 * Building actions according to FSA spec:
 * https://github.com/acdlite/flux-standard-action
 *
 * @public
 */

import { warn, debug } from '@ember/debug';
import * as ACTION_TYPES from './types';
import { next } from '@ember/runloop';
import { Schema, File } from './fetch';
import { lookup } from 'ember-dependency-lookup';
import fetchMetaValue from 'investigate-shared/actions/api/events/meta-values';
import { getFilter } from './filter-creators';
import { setFileStatus, getFileStatus } from 'investigate-shared/actions/api/file/file-status';
import _ from 'lodash';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const _handleError = (response, type) => {
  const warnResponse = JSON.stringify(response);
  warn(`_handleError ${type} ${warnResponse}`, { id: 'investigate-files.actions.data-creators' });
};

/**
 * Action creator that dispatches a set of actions for fetching files (with or without filters) and sorted by one field.
 * @method _fetchFiles
 * @private
 * @returns {function(*, *)}
 */
const _fetchFiles = () => {
  return (dispatch, getState) => {
    const { systemFilter, sortField, isSortDescending, pageNumber } = getState().files.fileList;
    const { expressionList } = getState().files.filter;
    dispatch({
      type: ACTION_TYPES.FETCH_NEXT_FILES,
      promise: File.fetchFiles(pageNumber, { sortField, isSortDescending }, systemFilter || expressionList),
      meta: {
        onSuccess: (response) => {
          const debugResponse = JSON.stringify(response);
          debug(`onSuccess: ${ACTION_TYPES.FETCH_NEXT_FILES} ${debugResponse}`);
        }
      }
    });
  };
};

/**
 * Action Creator to retrieve the paged files. Increments the current page number and updates the state.
 * @return {function} redux-thunk
 * @public
 */
const getPageOfFiles = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.INCREMENT_PAGE_NUMBER });
    dispatch(_fetchFiles());
  };
};

/**
 * Action Creator for fetching the first page of data. Before sending the request resets the state
 * @returns {function(*)}
 * @private
 */
const getFirstPageOfFiles = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.RESET_FILES });
    dispatch(getPageOfFiles());
  };
};

/**
 * Action creator for fetching an files schema.
 * @method fetchSchemaInfo
 * @public
 * @returns {Object}
 */
const fetchSchemaInfo = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SCHEMA_RETRIEVE,
      promise: Schema.fetchSchema(),
      meta: {
        onSuccess: (response) => {
          dispatch(initializeFilesPreferences());
          const debugResponse = JSON.stringify(response);
          debug(`onSuccess: ${ACTION_TYPES.SCHEMA_RETRIEVE} ${debugResponse}`);
        }
      }
    });
  };
};

const initializeFilesPreferences = () => {
  return (dispatch) => {
    const prefService = lookup('service:preferences');
    prefService.getPreferences('endpoint-preferences').then((data) => {
      if (data && data.filePreference) {
        // Only if preferences is sent from api, set the preference state.
        // Otherwise, initial state will be used.
        dispatch({
          type: ACTION_TYPES.SET_FILE_PREFERENCES,
          payload: data
        });
        const { sortField } = data.filePreference;
        if (sortField) {
          dispatch({
            type: ACTION_TYPES.SET_SORT_BY,
            payload: JSON.parse(sortField)
          });
        }
      }
      // Bellow code is temp, will be removed once server side preference is implemented
      const json = localStorage.getItem('investigatePreference');
      const storedState = json ? JSON.parse(json) : null;
      // *****/
      dispatch({ type: ACTION_TYPES.SET_QUERY_INPUT, payload: storedState || {} });
      dispatch(getFilter(getFirstPageOfFiles));
    });
  };
};

/**
 * Action creator for exporting files.
 * @public
 */
const exportFileAsCSV = () => {
  return (dispatch, getState) => {
    const { files } = getState();
    const { sortField, isSortDescending } = files.fileList;
    const { expressionList } = files.filter;
    dispatch({
      type: ACTION_TYPES.DOWNLOAD_FILE_AS_CSV,
      promise: File.fileExport({ sortField, isSortDescending }, expressionList, _getVisibleColumnNames(getState)),
      meta: {
        onSuccess: (response) => {
          const debugResponse = JSON.stringify(response);
          debug(`onSuccess: ${ACTION_TYPES.DOWNLOAD_FILE_AS_CSV} ${debugResponse}`);
        }
      }
    });
  };
};

const _getVisibleColumnNames = (getState) => {
  const { preferences: { filePreference } } = getState().preferences;
  return _.uniq(['firstFileName', ...filePreference.visibleColumns]);
};


const _setPreferences = (getState) => {
  const prefService = lookup('service:preferences');
  const { preferences } = getState().preferences;
  prefService.setPreferences('endpoint-preferences', null, { ...preferences });
};

const updateColumnVisibility = (column) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: column });
    next(() => {
      _setPreferences(getState);
    });
  };
};

/**
 * Action Creator to sort the files.
 * @return {function} redux-thunk
 * @public
 */
const sortBy = (sortField, isSortDescending) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.SET_SORT_BY, payload: { sortField, isSortDescending } });
    dispatch(getFirstPageOfFiles());
    _setPreferences(getState);
  };
};

/**
 * Action creator for for resetting the download link.
 * @public
 */
const resetDownloadId = () => ({ type: ACTION_TYPES.RESET_DOWNLOAD_ID });

const getAllServices = () => ({
  type: ACTION_TYPES.GET_LIST_OF_SERVICES,
  promise: File.getAllServices(),
  meta: {
    onFailure: (response) => _handleError(ACTION_TYPES.GET_LIST_OF_SERVICES, response)
  }
});

const setDataSourceTab = (tabName) => ({ type: ACTION_TYPES.CHANGE_DATASOURCE_TAB, payload: { tabName } });

const activeRiskSeverityTab = (tabName) => ({ type: ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB, payload: { tabName } });

const prepareQuery = (checksum, severity = 'Critical') => {
  const categoryValue = _.upperFirst(severity);
  return {
    filter: [
      { field: 'hash', value: checksum },
      { field: 'category', value: categoryValue }
    ]
  };
};

const getRiskScoreContext = (checksum, severity) => ({
  type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
  promise: File.getRiskScoreContext(prepareQuery(checksum, severity))
});

const fetchFileContext = (fileName) => {
  return (dispatch) => {
    const query = {
      filter: [
        { field: 'meta', value: 'FILE_NAME' },
        { field: 'value', value: fileName }
      ]
    };
    File.getContext(query, {
      initState: () => {
        dispatch({ type: ACTION_TYPES.CLEAR_PREVIOUS_CONTEXT });
      },
      onResponse: ({ data }) => {
        dispatch({ type: ACTION_TYPES.SET_CONTEXT_DATA, payload: data });
      },
      onError: ({ meta }) => {
        const error = (meta && meta.message) ? meta.message : 'context.error';
        dispatch({ type: ACTION_TYPES.CONTEXT_ERROR, payload: `investigateHosts.context.error.${error}` });
      }
    });
  };
};

const toggleFileSelection = (selectedFile) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_FILE, payload: selectedFile });

const selectAllFiles = () => ({ type: ACTION_TYPES.SELECT_ALL_FILES });

const deSelectAllFiles = () => ({ type: ACTION_TYPES.DESELECT_ALL_FILES });

const saveFileStatus = (checksums, data, callbacks = callbacksDefault) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SAVE_FILE_STATUS,
      promise: setFileStatus({ ...data, checksums }),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const getSavedFileStatus = (selections) => ({
  type: ACTION_TYPES.GET_FILE_STATUS,
  promise: getFileStatus(selections)
});


const fetchMachineCount = (checksums) => ({ type: ACTION_TYPES.GET_AGENTS_COUNT_SAGA, payload: checksums });

const _getMetaValues = (dispatch, { filter, queryNode, metaName, size = 1, onComplete }) => {
  const query = { ...queryNode };
  query.metaFilter = {
    conditions: [
      {
        meta: 'device.type',
        operator: '=',
        value: '\'nwendpoint\''
      },
      filter
    ]
  };

  const handlers = {
    onInit() {
      dispatch({ type: ACTION_TYPES.INIT_FETCH_HOST_NAME_LIST });
    },
    onError(dispatch) {
      dispatch({ type: ACTION_TYPES.FETCH_HOST_NAME_LIST_ERROR });
    },

    onResponse(response) {
      if (response) {
        const { data: _payload, meta } = response || {};
        const payload = Array.isArray(_payload) ? _payload : [];
        const description = meta ? meta.description : null;
        const percent = meta ? meta.percent : 0;
        if (description === 'Queued' || (description === 'Executing' || percent < 100 && payload.length === 0)) {
          return;
        } else {
          if (response.data) {
            dispatch({ type: ACTION_TYPES.META_VALUE_COMPLETE });
            onComplete(response.data);
          }

        }
      }
    }
  };
  fetchMetaValue(query, metaName, size, null, 1000, 10, handlers, 1);
};

const fetchHostNameList = (checksum) => {
  return (dispatch, getState) => {
    const queryNode = getState().investigateQuery;

    // Get the size for mata value
    const { fileList: { agentCountMapping } } = getState().files;
    const size = agentCountMapping && agentCountMapping[checksum] ? agentCountMapping[checksum] : 20;
    const input = {
      filter: { value: `(checksum = \'${checksum}\')` },
      queryNode,
      size,
      metaName: 'alias.host',
      onComplete: (data) => {
        dispatch({ type: ACTION_TYPES.SET_HOST_NAME_LIST, payload: data });
      }
    };
    _getMetaValues(dispatch, input);
  };
};

const fetchAgentId = (hostName, callBack) => {
  return (dispatch, getState) => {
    const queryNode = getState().investigateQuery;
    const input = {
      queryNode,
      filter: { value: `(alias.host = \'${hostName}\')` },
      size: 1,
      metaName: 'agent.id',
      onComplete: (data) => {
        if (callBack) {
          callBack(data);
        }
      }
    };
    _getMetaValues(dispatch, input);
  };
};

const setNewFileTab = (tabName) => ({ type: ACTION_TYPES.CHANGE_FILE_DETAIL_TAB, payload: { tabName } });

const getUpdatedRiskScoreContext = (checksum, tabName) => {
  return (dispatch) => {
    dispatch(activeRiskSeverityTab(tabName));
    dispatch(getRiskScoreContext(checksum, tabName));
  };
};

const retrieveRemediationStatus = (selections) => {
  const thumbprints = selections.mapBy('signature.thumbprint').compact();
  if (thumbprints && thumbprints.length) {
    return {
      type: ACTION_TYPES.FETCH_REMEDIATION_STATUS,
      promise: File.fetchRemediation({ thumbprints })
    };
  }
};

export {
  getPageOfFiles,
  sortBy,
  fetchSchemaInfo,
  exportFileAsCSV,
  updateColumnVisibility,
  resetDownloadId,
  getAllServices,
  initializeFilesPreferences,
  setDataSourceTab,
  fetchFileContext,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  saveFileStatus,
  fetchMachineCount,
  getSavedFileStatus,
  fetchHostNameList,
  fetchAgentId,
  setNewFileTab,
  getFirstPageOfFiles,
  activeRiskSeverityTab,
  getRiskScoreContext,
  getUpdatedRiskScoreContext,
  retrieveRemediationStatus
};
