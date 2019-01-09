import { lookup } from 'ember-dependency-lookup';
import * as ACTION_TYPES from '../types';
import { debug } from '@ember/debug';
import { fetchEndpointServers } from 'investigate-shared/actions/api/server';
import RSVP from 'rsvp';

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

// New endpoint server selection flow

export const pingEndpointServer = (dispatch) => {
  return new RSVP.Promise((resolve, reject) => {
    const request = lookup('service:request');
    request.ping('endpoint-server-ping')
      .then(function() {
        dispatch(isEndpointServerOffline(false));
        resolve({ isOnline: true });
      })
      .catch(function() {
        dispatch(isEndpointServerOffline(true));
        reject({ isOnline: false });
      });
  });
};

export const setupEndpointServer = () => {
  return async(dispatch) => {
    registerStreamOptions(null);
    const { isOnline } = await pingEndpointServer(dispatch);
    if (isOnline) {
      const persistedServerId = _getPersistedServerId();
      if (persistedServerId) {
        registerStreamOptions(persistedServerId);
        dispatch(setSelectedEndpointServer(persistedServerId));
      }
      // 2. Load all the server and set first as default sever
      await dispatch(_loadAllEndpointServers(persistedServerId));
      await pingEndpointServer(dispatch);
    }
  };
};

export const changeEndpointServer = ({ id }) => {
  return async(dispatch, getState) => {
    const { serverId } = getState().endpointQuery;
    if (serverId !== id) {
      _setPersistedServerId(id);
      registerStreamOptions(id);
      dispatch(setSelectedEndpointServer(id));
      await pingEndpointServer(dispatch);
    }
  };
};

export const _loadAllEndpointServers = (serverId) => {
  return async(dispatch, getState) => {
    registerStreamOptions(serverId);
    // Wait for all endpoint server to load
    await dispatch({
      type: ACTION_TYPES.LIST_OF_ENDPOINT_SERVERS,
      promise: fetchEndpointServers(),
      meta: {
        onSuccess: () => {
          if (!serverId) {
            // Set the server id and continue with that server id
            const [defaultServer] = getState().endpointServer.serviceData || [{}];
            _setPersistedServerId(defaultServer.id);
            registerStreamOptions(defaultServer.id);
            dispatch(setSelectedEndpointServer(defaultServer.id));
          }
        },
        onFailure: () => {
          clearStreamOptions();
        }
      }
    });
  };
};

const _getPersistedServerId = () => {
  const localStoragePersistedServerId = localStorage.getItem('endpoint:persistedServerId');
  const persistedServerId = localStoragePersistedServerId !== 'null' ? localStoragePersistedServerId : null;
  return persistedServerId;
};

const registerStreamOptions = (serviceId) => {
  const request = lookup('service:request');
  const socketUrlPostfix = serviceId ? serviceId : 'any';
  request.registerPersistentStreamOptions({ socketUrlPostfix, requiredSocketUrl: 'endpoint/socket' });
};

const clearStreamOptions = () => {
  const request = lookup('service:request');
  request.clearPersistentStreamOptions(['socketUrlPostfix', 'requiredSocketUrl']);
};

const _setPersistedServerId = (serverId) => {
  localStorage.setItem('endpoint:persistedServerId', serverId);
};


