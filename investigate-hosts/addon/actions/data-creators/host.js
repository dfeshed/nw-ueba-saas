import { lookup } from 'ember-dependency-lookup';
import { Machines } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';
import { isEmpty } from '@ember/utils';
import { resetHostDownloadLink } from 'investigate-hosts/actions/ui-state-creators';
import { initializeAgentDetails, setNewTabView } from 'investigate-hosts/actions/data-creators/details';
import { setupEndpointServer, changeEndpointServer } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import { parseQueryString } from 'investigate-shared/utils/query-util';
import _ from 'lodash';
import { next } from '@ember/runloop';
import { getFilter } from 'investigate-shared/actions/data-creators/filter-creators';
import { resetRiskContext, getRiskScoreContext, getRespondServerStatus } from 'investigate-shared/actions/data-creators/risk-creators';
import { getServiceId } from 'investigate-shared/actions/data-creators/investigate-creators';
import { getRestrictedFileList } from 'investigate-shared/actions/data-creators/file-status-creators';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import { toggleProcessDetailsView } from 'investigate-hosts/actions/data-creators/process';
import { toggleMftView, getFirstPageOfDownloads } from 'investigate-hosts/actions/data-creators/downloads';
import { extractHostColumns } from 'investigate-hosts/reducers/schema/selectors';

import { debug } from '@ember/debug';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const bootstrapInvestigateHosts = (query) => {
  return async(dispatch) => {
    try {
      // 1. Wait for endpoint server to load and availability
      await dispatch(setupEndpointServer());
      // 2. Endpoint server is online do other action
      // 2.1. Wait for user preference to load
      await dispatch(initializeHostsPreferences());
      // 2.2. Load list of machines
      dispatch(getPageOfMachines(query));
      // 3. Remaining data
      dispatch(getFilter(() => {}, 'MACHINE'));
      dispatch(getServiceId('MACHINE'));
      dispatch(getRestrictedFileList('MACHINE'));

    } catch (e) {
      // Endpoint server offline
    }
  };
};

const initializeHostDetailsPage = ({ sid, machineId, tabName = 'OVERVIEW', subTabName, pid, mftFile }, isPageLoading) => {
  return async(dispatch, getState) => {
    if (isPageLoading) {
      const id = sid || getState().endpointQuery.serverId;
      await dispatch(changeEndpointServer({ id }));
      // Wait for user preference to load
      await dispatch(initializeHostsPreferences());
    }

    dispatch(resetHostDownloadLink());

    if (isPageLoading) {
      dispatch(initializeAgentDetails({ agentId: machineId }, true, true, tabName));
    } else {
      dispatch(setNewTabView(tabName));
    }
    // To redirect to the Process details panel in the process tab
    next(() => {
      if (tabName === 'PROCESS' && subTabName === 'process-details') {
        dispatch(toggleProcessDetailsView({ pid: parseInt(pid, 10) }, true));
      }
      if (tabName === 'DOWNLOADS-MFT') {
        dispatch(toggleMftView(mftFile));
      } else if (tabName === 'DOWNLOADS') {
        dispatch(toggleMftView(mftFile));
        getFirstPageOfDownloads();
      }
    });
  };
};


const changeEndpointServerSelection = (server) => {
  return async(dispatch) => {
    try {
      await dispatch(changeEndpointServer(server));
      dispatch(resetHostDownloadLink());
      dispatch(getPageOfMachines());
      dispatch(getServiceId('MACHINE'));
      dispatch(getRestrictedFileList('MACHINE'));
    } catch (e) {
      // Endpoint server offline
    }
  };
};
/**
 * Action creator for polling all agent status
 * @method pollAgentStatus
 * @private
 * @returns {Object}
 */
const pollAgentStatus = () => {
  return (dispatch, getState) => {
    let { hostList } = getState().endpoint.machines;
    const { hostDetails } = getState().endpoint.overview;
    hostList = hostList || [];
    if (hostList.length <= 0 && !hostDetails) {
      return;
    }
    const machineAgentIds = hostList.map((host) => host.id);

    if (hostDetails && !machineAgentIds.find((id) => id === hostDetails.id)) {
      machineAgentIds.push(hostDetails.id);
    }
    dispatch({
      type: ACTION_TYPES.FETCH_AGENT_STATUS,
      promise: Machines.pollAgentStatus({ machineAgentIds }),
      meta: {
        onSuccess: (response) => {
          debug(`ACTION_TYPES.FETCH_AGENT_STATUS ${_stringifyObject(response)}`);
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_AGENT_STATUS, response);
        }
      }
    });
  };
};

/**
 * Action creator for fetching all Machines
 * @method getPageOfMachines
 * @public
 * @returns {Object}
 */
const getPageOfMachines = (query) => {
  return (dispatch, getState) => {

    if (query && !isEmpty(query)) {
      const expression = parseQueryString(query);
      const savedFilter = { id: -1, criteria: { expressionList: expression } };
      dispatch({ type: SHARED_ACTION_TYPES.SET_SAVED_FILTER, payload: savedFilter, meta: { belongsTo: 'MACHINE' } });
    }

    const { hostColumnSort } = getState().endpoint.machines;
    const { systemFilter, expressionList } = getState().endpoint.filter;

    dispatch({
      type: ACTION_TYPES.FETCH_ALL_MACHINES,
      promise: Machines.getPageOfMachines(-1, hostColumnSort, systemFilter || expressionList),
      meta: {
        onSuccess: (response) => {
          debug(`ACTION_TYPES.FETCH_ALL_MACHINES ${_stringifyObject(response)}`);
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_ALL_MACHINES, response);
        }
      }
    });

  };
};

/**
 * Action creator for fetching all the schemas
 * @method getAllSchemas
 * @public
 * @returns {Object}
 */
const getAllSchemas = () => {
  return (dispatch) => {
    dispatch({ type: ACTION_TYPES.FETCH_ALL_SCHEMAS,
      promise: Machines.getAllSchemas(),
      meta: {
        onSuccess: (response) => {
          debug(`ACTION_TYPES.FETCH_ALL_SCHEMAS ${_stringifyObject(response)}`);
          dispatch({ type: ACTION_TYPES.RESET_HOSTS });
          dispatch(initializeHostsPreferences());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_ALL_SCHEMAS, response);
        }
      }
    });
  };
};

/**
 * Action creator for fetching page wise Machines
 * @method getNextMachines
 * @public
 * @returns {Object}
 */
const getNextMachines = () => {
  return (dispatch, getState) => {
    const { hostColumnSort, pageNumber } = getState().endpoint.machines;
    const { systemFilter, expressionList } = getState().endpoint.filter;
    dispatch({
      type: ACTION_TYPES.FETCH_NEXT_MACHINES,
      promise: Machines.getPageOfMachines(pageNumber, hostColumnSort, systemFilter || expressionList),
      meta: {
        onSuccess: (response) => {
          debug(`ACTION_TYPES.FETCH_NEXT_MACHINES ${_stringifyObject(response)}`);
          dispatch(pollAgentStatus());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_NEXT_MACHINES, response);
        }
      }
    });
  };
};

/**
 * Action creator for fetch job id and download the csv from server
 * @method exportAsFile
 * @public
 * @returns {Object}
 */
const exportAsFile = () => {
  return (dispatch, getState) => {
    const { hostColumnSort } = getState().endpoint.machines;
    const { schema } = getState().endpoint.schema;
    const { expressionList } = getState().endpoint.filter;
    dispatch({
      type: ACTION_TYPES.FETCH_DOWNLOAD_JOB_ID,
      promise: Machines.downloadMachine(expressionList, schema, hostColumnSort, _getVisibleColumnNames(getState)),
      meta: {
        onSuccess: (response) => {
          debug(`ACTION_TYPES.FETCH_DOWNLOAD_JOB_ID ${_stringifyObject(response)}`);
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_DOWNLOAD_JOB_ID, response);
        }
      }
    });
  };
};

const _getVisibleColumnNames = (getState) => {
  const columns = extractHostColumns(getState());
  let savedColumns;
  if (columns && columns.length) {
    savedColumns = columns.map((column) => {
      if (column.field !== 'checkbox') {
        return column.field;
      }
    });
  }
  return savedColumns.compact();
};

const _setPreferences = (getState) => {
  const prefService = lookup('service:preferences');
  const { preferences } = getState().preferences;
  prefService.setPreferences('endpoint-preferences', null, { ...preferences }, null, { socketUrlPostfix: 'any' });
};

const saveColumnConfig = (tableId, changedProperty, columns) => {
  return (dispatch, getState) => {
    dispatch({ type: ACTION_TYPES.SAVE_COLUMN_CONFIG, payload: { tableId, changedProperty, columns } });
    next(() => {
      _setPreferences(getState);
    });
  };
};


const setHostColumnSort = (columnSort) => {
  return (dispatch, getState) => {
    // dispatch the actions to set selected sort
    dispatch({ type: ACTION_TYPES.SET_HOST_COLUMN_SORT, payload: columnSort });
    // reload the list with applied sort
    dispatch(getPageOfMachines());
    _setPreferences(getState);
  };
};


const deleteHosts = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const { selectedHostList } = getState().endpoint.machines;
    dispatch({
      type: ACTION_TYPES.DELETE_HOSTS,
      promise: Machines.deleteHosts(_.map(selectedHostList, 'id')),
      meta: {
        onSuccess: (response) => {
          debug(`ACTION_TYPES.DELETE_HOSTS ${_stringifyObject(response)}`);
          callbacks.onSuccess(response);
          dispatch(getPageOfMachines());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.DELETE_HOSTS, response);
          callbacks.onFailure(response);
        }
      }
    });
  };
};

const getAllServices = () => ({
  type: ACTION_TYPES.GET_LIST_OF_SERVICES,
  promise: Machines.getAllServices(),
  meta: {
    onSuccess: (response) => debug(`${ACTION_TYPES.GET_LIST_OF_SERVICES} ${_stringifyObject(response)}`),
    onFailure: (response) => handleError(ACTION_TYPES.GET_LIST_OF_SERVICES, response)
  }
});

const _stringifyObject = (data) => {
  return JSON.stringify(data);
};

const initializeHostsPreferences = () => {
  return async(dispatch) => {
    const prefService = lookup('service:preferences');
    await prefService.getPreferences('endpoint-preferences', null, { socketUrlPostfix: 'any' }).then((data) => {
      if (data && data.machinePreference) {
        // Only if preferences is sent from api, set the preference state.
        // Otherwise, initial state will be used.
        dispatch({
          type: ACTION_TYPES.SET_PREFERENCES,
          payload: data
        });
        const { sortField } = data.machinePreference;
        if (sortField) {
          dispatch({
            type: ACTION_TYPES.SET_HOST_COLUMN_SORT,
            payload: JSON.parse(sortField)
          });
        }
      }
    });
  };
};

const startScan = (agentIds, callbacks = callbacksDefault, serverId) => {
  Machines.startScanRequest(agentIds, serverId)
    .then(() => {
      callbacks.onSuccess();
    }).catch(({ meta: message }) => {
      callbacks.onFailure(message.message);
    });
};

const stopScan = (agentIds, callbacks = callbacksDefault, serverId) => {
  Machines.stopScanRequest(agentIds, serverId)
    .then(() => {
      callbacks.onSuccess();
    }).catch(({ meta: message }) => {
      callbacks.onFailure(message.message);
    });
};

const fetchHostContext = (machineName) => {
  return (dispatch) => {
    const query = {
      filter: [
        { field: 'meta', value: 'HOST' },
        { field: 'value', value: machineName }
      ]
    };
    Machines.getContext(query, {
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

const setFocusedHost = (item) => ({ type: ACTION_TYPES.SET_FOCUSED_HOST, payload: item });

const setFocusedHostIndex = (index) => ({ type: ACTION_TYPES.SET_FOCUSED_HOST_INDEX, payload: index });

const setHostListPropertyTab = (tabName) => ({ type: ACTION_TYPES.CHANGE_HOST_LIST_PROPERTY_TAB, payload: { tabName } });

const onHostSelection = (item) => {
  return (dispatch) => {
    dispatch(getRespondServerStatus());
    dispatch(setFocusedHost(item));
    dispatch(resetRiskContext());
    next(() => {
      dispatch(getRiskScoreContext(item.id, 'HOST'));
    });
  };
};

const downloadMFT = (agentId, serverId, callbacks = callbacksDefault) => {
  Machines.downloadMFT({ agentId, serverId })
    .then(() => {
      callbacks.onSuccess();
    }).catch(({ meta: message }) => {
      callbacks.onFailure(message.message);
    });
};


export {
  getAllServices,
  getAllSchemas,
  getPageOfMachines,
  getNextMachines,
  exportAsFile,
  setHostColumnSort,
  deleteHosts,
  initializeHostsPreferences,
  startScan,
  stopScan,
  fetchHostContext,
  onHostSelection,
  setHostListPropertyTab,
  setFocusedHostIndex,
  pollAgentStatus,
  setFocusedHost,
  bootstrapInvestigateHosts,
  changeEndpointServerSelection,
  initializeHostDetailsPage,
  downloadMFT,
  saveColumnConfig
};
