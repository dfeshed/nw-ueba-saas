import { Machines } from '../api';
import * as ACTION_TYPES from '../types';
import { handleError } from '../creator-utils';
import { isEmpty } from 'ember-utils';
import { setAppliedHostFilter, resetDetailsInputAndContent, resetHostDownloadLink } from 'investigate-hosts/actions/ui-state-creators';
import { addExternalFilter } from 'investigate-hosts/actions/data-creators/filter';
import { initializeAgentDetails, changeDetailTab } from 'investigate-hosts/actions/data-creators/details';
import { parseQueryString } from 'investigate-hosts/actions/utils/query-util';
import { lookup } from 'ember-dependency-lookup';
import _ from 'lodash';
import Ember from 'ember';
const { Logger } = Ember;

const callbacksDefault = { onSuccess() {}, onFailure() {} };

/**
 * Action creator for notifying all agent status
 * @method notifyAgentStatus
 * @private
 * @returns {Object}
 */
const _notifyAgentStatus = () => {
  return (dispatch, getState) => {
    const { hostList, stopAgentStream } = getState().endpoint.machines;
    if (stopAgentStream) {
      stopAgentStream();
    }
    if (hostList.length <= 0) {
      return;
    }
    Machines.notifyAgentStatus({
      onInit: (stopStreamFn) => dispatch({ type: ACTION_TYPES.FETCH_AGENT_STATUS_STREAM_INITIALIZED, payload: stopStreamFn }),
      onResponse: (payload) => dispatch({ type: ACTION_TYPES.FETCH_AGENT_STATUS, payload })
    });
  };
};

/**
 * Action creator for fetching all Machines
 * @method getPageOfMachines
 * @public
 * @returns {Object}
 */
const getPageOfMachines = () => {
  return (dispatch, getState) => {
    const { hostColumnSort } = getState().endpoint.machines;
    const { systemFilter, expressionList } = getState().endpoint.filter;
    dispatch({
      type: ACTION_TYPES.FETCH_ALL_MACHINES,
      promise: Machines.getPageOfMachines(-1, hostColumnSort, systemFilter || expressionList),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.FETCH_ALL_MACHINES, response);
          dispatch(_notifyAgentStatus());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_ALL_MACHINES, response);
        }
      }
    });
  };
};

/**
 * Action creator for fetching all filters
 * @method getAllFilters
 * @private
 * @returns {Object}
 */
const _getAllFilters = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FETCH_ALL_FILTERS,
      promise: Machines.getAllFilters(),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.FETCH_ALL_FILTERS, response);
          dispatch({ type: ACTION_TYPES.EXECUTE_QUERY });
          dispatch(getPageOfMachines());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_ALL_FILTERS, response);
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
          Logger.debug(ACTION_TYPES.FETCH_ALL_SCHEMAS, response);
          dispatch(_getAllFilters());
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
          Logger.debug(ACTION_TYPES.FETCH_NEXT_MACHINES, response);
          dispatch(_notifyAgentStatus());
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_NEXT_MACHINES, response);
        }
      }
    });
  };
};

/**
 * Action creator for creating custom search
 * @method createCustomSearch
 * @public
 * @returns {Object}
 */
const createCustomSearch = (filter, callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const { filterSelected, visibleSchemas } = getState().endpoint.filter;
    dispatch({
      type: ACTION_TYPES.CREATE_CUSTOM_SEARCH,
      promise: Machines.createCustomSearch(filterSelected, visibleSchemas, filter),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.CREATE_CUSTOM_SEARCH, response);
          callbacks.onSuccess(response);
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.CREATE_CUSTOM_SEARCH, response);
          callbacks.onFailure(response);
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
    const { filterSelected, hostColumnSort } = getState().endpoint.machines;
    const { schema } = getState().endpoint.schema;
    const columns = schema.filterBy('defaultProjection', true).mapBy('name');
    dispatch({
      type: ACTION_TYPES.FETCH_DOWNLOAD_JOB_ID,
      promise: Machines.downloadMachine(filterSelected, schema, hostColumnSort, columns),
      meta: {
        onSuccess: (response) => {
          Logger.debug(ACTION_TYPES.FETCH_DOWNLOAD_JOB_ID, response);
        },
        onFailure: (response) => {
          handleError(ACTION_TYPES.FETCH_DOWNLOAD_JOB_ID, response);
        }
      }
    });
  };
};

const updateColumnVisibility = (column) => ({ type: ACTION_TYPES.UPDATE_COLUMN_VISIBILITY, payload: column });

const setHostColumnSort = (columnSort) => {
  return (dispatch) => {
    // dispatch the actions to set selected sort
    dispatch({ type: ACTION_TYPES.SET_HOST_COLUMN_SORT, payload: columnSort });

    // reload the list with applied sort
    dispatch(getPageOfMachines());
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
          Logger.debug(ACTION_TYPES.DELETE_HOSTS, response);
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

const initializeHostPage = ({ machineId, filterId, tabName = 'OVERVIEW', query }) => {
  return (dispatch) => {
    // On clicking the host name setting the machineId in the URL, on close removing the it from url
    if (machineId && !isEmpty(machineId)) {
      dispatch(initializeAgentDetails({ agentId: machineId }, true));
      dispatch(changeDetailTab(tabName));
      dispatch(resetHostDownloadLink());
    } else {
      // Resetting the details data and input data
      dispatch(resetDetailsInputAndContent());
    }
    if (filterId && !isEmpty(filterId)) {
      dispatch(setAppliedHostFilter(filterId, true));
    }
    // Parse the query string and set the filter
    if (query && !isEmpty(query)) {
      dispatch(addExternalFilter(parseQueryString(query)));
    }
  };
};

const initializeHostsPreferences = () => {
  return (dispatch) => {
    const prefService = lookup('service:preferences');
    dispatch({
      type: ACTION_TYPES.GET_PREFERENCES,
      promise: prefService.getPreferences('endpoint-preferences'),
      meta: {
        onSuccess: (data) => {
          if (data && data.machinePreference) {
            // Only if preferences is sent from api, set the preference state.
            // Otherwise, initial state will be used.
            const { sortField } = data.machinePreference;
            if (sortField) {
              dispatch({
                type: ACTION_TYPES.SET_HOST_COLUMN_SORT,
                payload: { key: sortField, descending: false }
              });
            }
          }
        }
      }
    });
  };
};

export {
  getAllSchemas,
  getPageOfMachines,
  getNextMachines,
  createCustomSearch,
  exportAsFile,
  updateColumnVisibility,
  setHostColumnSort,
  deleteHosts,
  initializeHostPage,
  initializeHostsPreferences
};

