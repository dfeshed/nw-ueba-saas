import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from '../types';
import { debug } from '@ember/debug';
import { fetchEndpointServers } from 'investigate-shared/actions/api/server';

export const _initializeEndpoint = (serverId, callback) => {
  return (dispatch) => {
    const request = lookup('service:request');
    localStorage.setItem('endpoint:persistedServerId', serverId);
    request.registerPersistentStreamOptions({ socketUrlPostfix: serverId, requiredSocketUrl: 'endpoint/socket' });
    dispatch(callback());
  };
};

export const isEndpointServerOffline = (status) => ({
  type: ACTION_TYPES.ENDPOINT_SERVER_STATUS,
  payload: status
});

export const setEndpointServer = (isDispatchedFromServiceSelector, server, callback) => {
  return (dispatch, getState) => {
    const { serverId } = getState().endpointQuery;
    if (!isDispatchedFromServiceSelector) {
      // most probably coming from the getEndpointServers call
      if (serverId) {
        dispatch(_initializeEndpoint(serverId, callback));
      } else {
        const [defaultServer] = getState().endpointServer.serviceData || [{}];
        // called when serverId is not present in the state
        dispatch(setSelectedEndpointServer(defaultServer.id));
        dispatch(_initializeEndpoint(defaultServer.id, callback));
      }
    } else if (server && serverId !== server.id) {
      dispatch(setSelectedEndpointServer(server.id));
      dispatch(_initializeEndpoint(server.id, callback));
    }
  };
};

export const getEndpointServers = (callback) => {
  return (dispatch) => {
    const request = lookup('service:request');
    request.registerPersistentStreamOptions({ socketUrlPostfix: 'any', requiredSocketUrl: 'endpoint/socket' });
    dispatch({
      type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS,
      promise: fetchEndpointServers(),
      meta: {
        onSuccess: (response) => {
          const localStoragePersistedServerId = localStorage.getItem('endpoint:persistedServerId');
          const persistedServerId = localStoragePersistedServerId != 'null' ? localStoragePersistedServerId : null;
          if (persistedServerId) {
            dispatch(setSelectedEndpointServer(persistedServerId));
          }
          dispatch(callback());
          debug(`onSuccess: ${response}`);
        },
        onfailure: () => {
          request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
        }
      }
    });
  };
};

export const setSelectedEndpointServer = (id) => ({ type: ACTION_TYPES.ENDPOINT_SERVER_SELECTED, payload: id });

export const setSelectedMachineServerId = (id) => ({ type: ACTION_TYPES.SET_SELECTED_MACHINE_SERVER_ID, payload: id });

