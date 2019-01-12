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
import { setFileStatus, getFileStatus } from 'investigate-shared/actions/api/file/file-status';
import { setSelectedEndpointServer, setupEndpointServer, changeEndpointServer } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import { getFilter } from 'investigate-shared/actions/data-creators/filter-creators';
import { resetRiskContext, getRiskScoreContext, getRespondServerStatus } from 'investigate-shared/actions/data-creators/risk-creators';
import { buildTimeRange } from 'investigate-shared/utils/time-util';
import { getRestrictedFileList } from 'investigate-shared/actions/data-creators/file-status-creators';
import { checksumsWithoutRestricted } from 'investigate-shared/utils/file-status-util';
import { getServiceId } from 'investigate-shared/actions/data-creators/investigate-creators';
import { getCertificates } from 'investigate-files/actions/certificate-data-creators';
import { getFileAnalysisData } from 'investigate-shared/actions/data-creators/file-analysis-creators';
import { setNewFileTab } from 'investigate-files/actions/visual-creators';
import { failure } from 'investigate-shared/utils/flash-messages';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const _handleError = (response, type) => {
  const warnResponse = JSON.stringify(response);
  warn(`_handleError ${type} ${warnResponse}`, { id: 'investigate-files.actions.data-creators' });
};

/**
 * Bootstraping investigate files page, loads all the endpoint server and checks for availability
 * @returns {Function}
 */
const bootstrapInvestigateFiles = () => {
  return async(dispatch) => {
    try {
      // 1. Wait for endpoint server to load and availability
      await dispatch(setupEndpointServer());
      // 2. Endpoint server is online do other action
      // 2.1. Wait for user preference to load
      await dispatch(initializeFilesPreferences());
      // 2.2. Load list of files
      dispatch(getFirstPageOfFiles());
      // 3. Fetch remaining required data
      dispatch(getFilter(() => {}, 'FILE'));
      dispatch(getRestrictedFileList('FILE'));
      dispatch(triggerFileActions());
    } catch (e) {
      // Endpoint server offline
    }

  };
};

const changeEndpointServerSelection = (server) => {
  return async(dispatch) => {
    try {
      await dispatch(changeEndpointServer(server));
      dispatch(getFirstPageOfFiles());
      dispatch(triggerFileActions());
    } catch (e) {
      // Endpoint server offline
    }
  };
};
/**
 * Action creator that dispatches a set of actions for fetching files (with or without filters) and sorted by one field.
 * @method _fetchFiles
 * @private
 * @returns {function(*, *)}
 */
const _fetchFiles = (type) => {
  return (dispatch, getState) => {
    const { systemFilter, sortField, isSortDescending, pageNumber } = getState().files.fileList;
    const { expressionList } = getState().files.filter;
    dispatch({
      type,
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

const initializeFileDetails = (checksum) => {
  return (dispatch, getState) => {
    const { files: { filter } } = getState();
    //  To fix the filter reload issue we need to set the applied filter as a saved filter
    if (!filter.selectedFilter || filter.selectedFilter.id === -1) {
      const savedFilter = { id: 1, criteria: { expressionList: filter.expressionList } };
      dispatch({ type: SHARED_ACTION_TYPES.SET_SAVED_FILTER, payload: savedFilter, meta: { belongsTo: 'FILE' } });
    }

    dispatch(getServiceId('FILE', () => {
      dispatch(_fetchHostNameList(checksum));
    }));
    dispatch(getRespondServerStatus());
    dispatch(resetRiskContext());
    dispatch(getRiskScoreContext(checksum, 'FILE'));
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
    dispatch(_fetchFiles(ACTION_TYPES.FETCH_NEXT_FILES));
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
    dispatch({ type: ACTION_TYPES.INCREMENT_PAGE_NUMBER });
    dispatch(_fetchFiles(ACTION_TYPES.FETCH_ALL_FILES));
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
    });
  };
};

const triggerFileActions = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.RESET_CERTIFICATES });
    dispatch({ type: ACTION_TYPES.CLOSE_CERTIFICATE_VIEW });
    dispatch(getServiceId('FILE'));
    dispatch(getCertificates());
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
  return ['firstFileName', 'score', ...filePreference.visibleColumns];
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

const toggleFileSelection = (selectedFile) => ({ type: ACTION_TYPES.TOGGLE_SELECTED_FILE, payload: selectedFile });

const selectAllFiles = () => ({ type: ACTION_TYPES.SELECT_ALL_FILES });

const deSelectAllFiles = () => ({ type: ACTION_TYPES.DESELECT_ALL_FILES });

const saveFileStatus = (checksums, data, callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const state = getState();
    const { fileStatus: { restrictedFileList }, files: { fileList: { selectedFileList } } } = state;
    if (data.fileStatus === 'Whitelist') {
      checksums = checksumsWithoutRestricted(selectedFileList, restrictedFileList);
    }
    // Selecting top 100 checksums only for file status change.
    if (checksums && checksums.length > 100) {
      checksums = checksums.slice(0, 100);
    }
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

const _setSelectedFile = (item) => ({ type: ACTION_TYPES.SET_SELECTED_FILE, payload: item });

const onFileSelection = (item) => {
  return (dispatch) => {
    dispatch(getRespondServerStatus());
    dispatch(_setSelectedFile(item));
    dispatch(resetRiskContext());
    next(() => {
      dispatch(getRiskScoreContext(item.checksumSha256, 'FILE'));
      dispatch(_getSelectedFileProperties(item.checksumSha256));
    });
    dispatch(_fetchHostNameList(item.checksumSha256));
  };
};

const _getMetaValues = (dispatch, { filter, queryNode, metaName, size = 1, onComplete }) => {
  const query = { ...queryNode };

  // Now we will be provided with only timeRange, later we might add startTime and EndTime
  if (query.timeRange) {
    const { timeRange: { value, unit } } = query;
    const timeZone = lookup('service:timezone');
    const { zoneId } = timeZone.get('selected');
    const { startTime, endTime } = buildTimeRange(value, unit, zoneId);
    query.startTime = startTime;
    query.endTime = endTime;
  }

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

const _fetchHostNameList = (checksum) => {
  return (dispatch, getState) => {
    const queryNode = getState().investigate;

    // Get the size for mata value
    const { fileList: { agentCountMapping } } = getState().files;
    const size = agentCountMapping && agentCountMapping[checksum] ? agentCountMapping[checksum] : 200;
    const input = {
      filter: { value: `(checksum.all = '${checksum}')` },
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
    const queryNode = getState().investigate;
    const input = {
      queryNode,
      filter: { value: `(alias.host = '${hostName}')` },
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


const retrieveRemediationStatus = (selections) => {
  const thumbprints = selections.mapBy('signature.thumbprint').compact();
  if (thumbprints && thumbprints.length) {
    return {
      type: ACTION_TYPES.FETCH_REMEDIATION_STATUS,
      promise: File.fetchRemediation({ thumbprints })
    };
  }
};

const userLeftFilesPage = () => ({ type: ACTION_TYPES.USER_LEFT_FILES_PAGE });

const _getSelectedFileProperties = (checksum) => {
  return {
    type: ACTION_TYPES.INITIALIZE_FILE_DETAIL,
    promise: File.getSelectedFileProperties(checksum)
  };
};

const setSelectedIndex = (index) => ({ type: ACTION_TYPES.SET_SELECTED_INDEX, payload: index });

const initializerForFileDetailsAndAnalysis = (checksum, sid, tabName, fileFormat) => {
  return (dispatch, getState) => {
    const request = lookup('service:request');
    dispatch({ type: ACTION_TYPES.RESET_INPUT_DATA });
    request.registerPersistentStreamOptions({ socketUrlPostfix: sid, requiredSocketUrl: 'endpoint/socket' });
    const { files: {
      fileList: { selectedDetailFile, listOfServices },
      risk: { alertsLoadingStatus },
      fileAnalysis: { fileData, filePropertiesData }
    } } = getState();

    if (!selectedDetailFile) {
      dispatch(_getSelectedFileProperties(checksum));
    }

    if (tabName === 'ANALYSIS') {

      dispatch(setNewFileTab(tabName));

      if (!fileData || !filePropertiesData) {
        const callBackOptions = {
          onFailure: (message) => failure(message, null, false)
        };
        dispatch(getFileAnalysisData(checksum, fileFormat, callBackOptions));
      }

    } else {

      if (!listOfServices || !alertsLoadingStatus) {
        dispatch(getAllServices());
        dispatch(setSelectedEndpointServer(sid));
        dispatch(initializeFileDetails(checksum));
      }

    }
  };
};

const downloadFilesToServer = (checksumSha256, callbacks) => {
  return (dispatch, getState) => {
    const queryNode = getState().investigate;
    const input = {
      filter: { value: `(checksum.all = '${checksumSha256}')` },
      queryNode,
      size: 100,
      metaName: 'agent.id',
      onComplete: (data) => {
        const agentIds = data ? data.map((d) => d.value) : [];
        if (agentIds.length) {
          File.sendFileDownloadToServerRequest({ checksumSha256, agentIds })
            .then(() => {
              callbacks.onSuccess();
            }).catch(({ meta: message }) => {
              callbacks.onFailure(message.message);
            });
        }
      }
    };
    _getMetaValues(dispatch, input);
  };
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
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  saveFileStatus,
  fetchMachineCount,
  getSavedFileStatus,
  fetchAgentId,
  getFirstPageOfFiles,
  retrieveRemediationStatus,
  onFileSelection,
  userLeftFilesPage,
  setDataSourceTab,
  initializeFileDetails,
  setSelectedIndex,
  triggerFileActions,
  initializerForFileDetailsAndAnalysis,
  bootstrapInvestigateFiles,
  changeEndpointServerSelection,
  downloadFilesToServer
};
